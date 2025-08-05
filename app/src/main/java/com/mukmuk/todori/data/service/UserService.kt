package com.mukmuk.todori.data.service

import com.google.firebase.firestore.FirebaseFirestore
import com.mukmuk.todori.data.remote.user.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserService @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private fun UserDoc(uid: String) =
        firestore.collection("users").document(uid)

    //프로필 조회
    suspend fun getProfile(uid: String): User? {
        val snapshot = UserDoc(uid).get().await()
        return snapshot.toObject(User::class.java)
    }
}