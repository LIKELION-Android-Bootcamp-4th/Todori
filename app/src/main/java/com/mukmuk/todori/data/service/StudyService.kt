package com.mukmuk.todori.data.service

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.mukmuk.todori.data.remote.study.MyStudy
import com.mukmuk.todori.data.remote.study.Study
import com.mukmuk.todori.data.remote.study.StudyMember
import com.mukmuk.todori.data.remote.study.StudyTodo
import com.mukmuk.todori.data.remote.study.TodoProgress
import kotlinx.coroutines.tasks.await

class StudyService(
    private val firestore: FirebaseFirestore
) {
    private fun studiesRef(): CollectionReference = firestore.collection("studies")

    suspend fun createStudy(study: Study, leaderMember: StudyMember,myStudy: MyStudy,uid: String): String {
        val ref = studiesRef().document()
        val autoId = ref.id
        val studyWithId = study.copy(studyId = autoId)
        val leaderMemberWithStudyId = leaderMember.copy(studyId = autoId)
        val myStudyWithStudyId = myStudy.copy(studyId = autoId)
        ref.set(studyWithId, SetOptions.merge()).await()
        firestore.collection("studyMembers").document("${autoId}_${leaderMember.uid}")
            .set(leaderMemberWithStudyId, SetOptions.merge()).await()
        firestore.collection("users").document(uid).collection("myStudies").document(autoId)
            .set(myStudyWithStudyId, SetOptions.merge()).await()
        return autoId

    }

    suspend fun getMyStudies(uid: String): List<MyStudy> {
        return firestore.collection("users")
            .document(uid)
            .collection("myStudies")
            .get().await()
            .documents.mapNotNull { it.toObject(MyStudy::class.java) }
    }

    suspend fun getStudies(studyIds: List<String>): List<Study> {
        if (studyIds.isEmpty()) return emptyList()
        val result = mutableListOf<Study>()
        for (studyIdList in studyIds.chunked(10)) {
            val snapshot = firestore.collection("studies")
                .whereIn("studyId", studyIdList)
                .get().await()
            result.addAll(snapshot.documents.mapNotNull { it.toObject(Study::class.java) })
        }
        return result
    }

    // 2. 여러 studyId의 StudyTodo 한 번에
    suspend fun getTodosForStudies(studyIds: List<String>, date: String? = null): List<StudyTodo> {
        if (studyIds.isEmpty()) return emptyList()
        val result = mutableListOf<StudyTodo>()
        for (studyIdList in studyIds.chunked(10)) {
            val ref = firestore.collection("studyTodos")
                .whereIn("studyId", studyIdList)
            val snapshot = if (date != null) {
                ref.whereEqualTo("date", date).get().await()
            } else {
                ref.get().await()
            }
            result.addAll(snapshot.documents.mapNotNull { it.toObject(StudyTodo::class.java) })
        }
        return result
    }

    suspend fun getMyAllProgresses(uid: String): List<TodoProgress> {
        val snapshot = firestore.collection("todoProgresses")
            .whereEqualTo("uid", uid)
            .get().await()
        return snapshot.documents.mapNotNull { doc ->
            val data = doc.data ?: return@mapNotNull null
            val isDoneRaw = data["isDone"] ?: data["done"]
            val isDone = when (isDoneRaw) {
                is Boolean -> isDoneRaw
                else -> false
            }
            TodoProgress(
                studyTodoId = data["studyTodoId"] as? String ?: "",
                studyId = data["studyId"] as? String ?: "",
                uid = data["uid"] as? String ?: "",
                date = data["date"] as? String ?: "",
                done = isDone,
                completedAt = data["completedAt"] as? com.google.firebase.Timestamp
            )
        }
    }

    suspend fun getProgressByUidStudyDate(uid: String, studyId: String, date: String): List<TodoProgress> {
        val snapshot = firestore
            .collection("todoProgresses")
            .whereEqualTo("uid", uid)
            .whereEqualTo("studyId", studyId)
            .whereEqualTo("date", date)
            .get().await()
        return snapshot.documents.mapNotNull { it.toObject(TodoProgress::class.java) }
    }


    suspend fun getMembersForStudies(studyIds: List<String>): List<StudyMember> {
        if (studyIds.isEmpty()) return emptyList()
        val result = mutableListOf<StudyMember>()
        for (batch in studyIds.chunked(10)) {
            val snapshot = firestore.collection("studyMembers")
                .whereIn("studyId", batch)
                .get().await()
            result.addAll(snapshot.documents.mapNotNull { it.toObject(StudyMember::class.java) })
        }
        return result
    }

    suspend fun getProgressesByStudyAndDate(studyId: String, date: String): List<TodoProgress> {
        val snapshot = firestore.collection("todoProgresses")
            .whereEqualTo("studyId", studyId)
            .whereEqualTo("date", date)
            .get().await()
        return snapshot.documents.mapNotNull { it.toObject(TodoProgress::class.java) }
    }

    suspend fun toggleTodoProgressDone(studyId: String, studyTodoId: String, uid: String, checked: Boolean) {
        val docId = "progress_${studyTodoId}_$uid"
        firestore.collection("todoProgresses")
            .document(docId)
            .update("done", checked)
            .await()
    }

    suspend fun deleteStudyTodo(studyTodoId: String) {
        firestore.collection("studyTodos").document(studyTodoId).delete().await()
        val progressesSnapshot = firestore.collection("todoProgresses")
            .whereEqualTo("studyTodoId", studyTodoId)
            .get().await()
        for (doc in progressesSnapshot.documents) {
            doc.reference.delete().await()
        }
    }

    suspend fun addStudyTodoWithProgress(studyTodo: StudyTodo, members: List<StudyMember>) {
        // 1. studyTodos 컬렉션에 추가 (Auto-ID 필요시 set 대신 add)
        val todoRef = firestore.collection("studyTodos").document() // auto-id 생성
        val autoId = todoRef.id
        val todoWithId = studyTodo.copy(studyTodoId = autoId)
        todoRef.set(todoWithId).await()

        for (member in members) {
            val progressId = "progress_${autoId}_${member.uid}"
            val progress = mapOf(
                "studyTodoId" to autoId,
                "studyId" to studyTodo.studyId,
                "uid" to member.uid,
                "done" to false,
                "completedAt" to null,
                "date" to studyTodo.date
            )
            firestore.collection("todoProgresses").document(progressId).set(progress).await()
        }
    }

    suspend fun deleteStudyWithAllData(studyId: String) {
        val batch = firestore.batch()
        // studies/{studyId} 삭제
        val studyRef = firestore.collection("studies").document(studyId)
        batch.delete(studyRef)

        // studyMembers(studyId) 삭제
        val members = firestore.collection("studyMembers")
            .whereEqualTo("studyId", studyId).get().await()
        for (doc in members.documents) {
            batch.delete(doc.reference)
        }

        // studyTodos(studyId) 삭제
        val todos = firestore.collection("studyTodos")
            .whereEqualTo("studyId", studyId).get().await()
        for (doc in todos.documents) {
            batch.delete(doc.reference)
        }

        // todoProgresses(studyId) 삭제
        val progresses = firestore.collection("todoProgresses")
            .whereEqualTo("studyId", studyId).get().await()
        for (doc in progresses.documents) {
            batch.delete(doc.reference)
        }

        // users/{uid}/myStudies 내역 삭제
        val allMembers = members.documents.mapNotNull { it.getString("uid") }
        for (uid in allMembers) {
            val myStudyRef = firestore.collection("users").document(uid)
                .collection("myStudies").document(studyId)
            batch.delete(myStudyRef)
        }

        batch.commit().await()
    }

    suspend fun updateStudy(study: Study) {
        val docId = study.studyId.ifBlank { throw IllegalArgumentException("studyId required") }
        firestore.collection("studies").document(docId)
            .set(study, SetOptions.merge())
            .await()
    }

    suspend fun updateMyStudyNickname(uid: String, studyId: String, nickname: String) {
        val myStudyRef = firestore.collection("users")
            .document(uid)
            .collection("myStudies")
            .document(studyId)

        myStudyRef.update("nickname", nickname).await()
    }

    suspend fun leaveStudy(studyId: String, uid: String) {
        val batch = firestore.batch()

        val myStudyRef = firestore.collection("users")
            .document(uid)
            .collection("myStudies")
            .document(studyId)
        batch.delete(myStudyRef)

        val studyMemberRef = firestore.collection("studyMembers")
            .document("${studyId}_${uid}")
        batch.delete(studyMemberRef)

        val progresses = firestore.collection("todoProgresses")
            .whereEqualTo("studyId", studyId)
            .whereEqualTo("uid", uid)
            .get()
            .await()
        for (doc in progresses.documents) {
            batch.delete(doc.reference)
        }

        batch.commit().await()
    }


    suspend fun updateHasPosted(uid: String, studyId: String, hasPosted: Boolean) {
        firestore.collection("users")
            .document(uid)
            .collection("myStudies")
            .document(studyId)
            .update("hasPosted", hasPosted)
    }
}
