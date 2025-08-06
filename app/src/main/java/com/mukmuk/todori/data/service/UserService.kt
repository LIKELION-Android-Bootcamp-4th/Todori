package com.mukmuk.todori.data.service

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.mukmuk.todori.data.remote.goal.Goal
import com.mukmuk.todori.data.remote.user.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserService @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private fun userDoc(uid: String) =
        firestore.collection("users").document(uid)

    //프로필 조회
    suspend fun getProfile(uid: String): User? {
        val snapshot = userDoc(uid).get().await()
        return snapshot.toObject(User::class.java)
    }

    //프로필 수정
    suspend fun updateUser(uid: String, nickname: String, intro: String) {
        userDoc(uid).set(
            mapOf("nickname" to nickname, "intro" to intro),
            SetOptions.merge())
            .await()
    }

    //목표 조회
    suspend fun getGoals(uid: String): List<Goal>? {
        return userDoc(uid)
            .collection("goals")
            .get()
            .await()
            .documents
            .mapNotNull { it.toObject(Goal::class.java) }
    }

}