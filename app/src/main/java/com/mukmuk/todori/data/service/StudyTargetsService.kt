package com.mukmuk.todori.data.service

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.mukmuk.todori.data.remote.user.StudyTargets
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StudyTargetsService @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    private fun studyTargetsRef(uid: String) =
        firestore.collection("users")
            .document(uid)
            .collection("studyTargets")
            .document("default")


    fun targetsFlow(uid: String): Flow<StudyTargets?> = callbackFlow {
        val reg = studyTargetsRef(uid).addSnapshotListener { snap, err ->
            if (err != null) {
                close(err)
                return@addSnapshotListener
            }
            trySend(snap?.toObject(StudyTargets::class.java))
        }
        awaitClose { reg.remove() }
    }

    suspend fun getStudyTargets(uid: String): StudyTargets? {
        return try {
            val snapshot = studyTargetsRef(uid).get().await()
            snapshot.toObject(StudyTargets::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateStudyTargets(uid: String, targets: StudyTargets) {
        val updatedTargets = targets.copy(updatedAt = Timestamp.now())
        studyTargetsRef(uid).set(updatedTargets).await()
    }


}