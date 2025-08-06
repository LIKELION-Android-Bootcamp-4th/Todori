package com.mukmuk.todori.data.service

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.mukmuk.todori.data.remote.study.Study
import com.mukmuk.todori.data.remote.study.StudyMember
import kotlinx.coroutines.tasks.await

class StudyService(
    private val firestore: FirebaseFirestore
) {
    private fun studiesRef(): CollectionReference = firestore.collection("studies")

    suspend fun createStudy(study: Study, leaderMember: StudyMember): String {
        val ref = studiesRef().document()
        val autoId = ref.id
        val studyWithId = study.copy(studyId = autoId)
        ref.set(studyWithId, SetOptions.merge()).await()
        //나를 leader로 등록
        ref.collection("members").document(leaderMember.uid)
            .set(leaderMember, SetOptions.merge()).await()
        return autoId
    }
}
