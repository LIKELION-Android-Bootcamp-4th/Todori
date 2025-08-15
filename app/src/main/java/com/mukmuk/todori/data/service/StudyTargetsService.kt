package com.mukmuk.todori.data.service

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.mukmuk.todori.data.remote.user.StudyTargets
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

    suspend fun updateDailyTarget(uid: String, minutes: Int) {
        val current = getStudyTargets(uid) ?: StudyTargets()
        val updated = current.copy(
            dailyMinutes = minutes,
            updatedAt = Timestamp.now()
        )
        studyTargetsRef(uid).set(updated).await()
    }

    suspend fun updateWeeklyTarget(uid: String, minutes: Int) {
        val current = getStudyTargets(uid) ?: StudyTargets()
        val updated = current.copy(
            weeklyMinutes = minutes,
            updatedAt = Timestamp.now()
        )
        studyTargetsRef(uid).set(updated).await()
    }

    suspend fun updateMonthlyTarget(uid: String, minutes: Int) {
        val current = getStudyTargets(uid) ?: StudyTargets()
        val updated = current.copy(
            monthlyMinutes = minutes,
            updatedAt = Timestamp.now()
        )
        studyTargetsRef(uid).set(updated).await()
    }
}