package com.mukmuk.todori.data.service

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
    private fun studiesRef() = firestore.collection("studies")

    suspend fun createStudy(study: Study, leaderMember: StudyMember): String {
        val ref = studiesRef().document()
        val autoId = ref.id
        val studyWithId = study.copy(studyId = autoId)
        ref.set(studyWithId, SetOptions.merge()).await()
        ref.collection("members").document(leaderMember.uid)
            .set(leaderMember, SetOptions.merge()).await()
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


}
