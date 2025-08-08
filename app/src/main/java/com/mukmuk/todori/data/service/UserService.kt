package com.mukmuk.todori.data.service

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.mukmuk.todori.data.remote.user.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class UserService @Inject constructor(
    private val firestore: FirebaseFirestore
){
    private fun userRef(uid: String): CollectionReference =
        firestore.collection("users").document(uid).collection(uid)

    suspend fun createUser(uid: String, user: User) {
        val ref = userRef(uid).document()
        ref.set(user)
            .addOnSuccessListener {
                println("Firestore: 새로운 사용자(${user.uid}) 정보 성공적으로 생성.")
            }
            .addOnFailureListener { e ->
                println("Firestore: 새로운 사용자 정보 생성 실패: $e")
            }
    }

    suspend fun getUser(uid: String) {

    }

    suspend fun updateUser(uid: String, user: User) {
        val ref = userRef(uid).document()
        ref.set(user, SetOptions.merge())
            .addOnSuccessListener {
                println("Firestore: 기존 사용자(${user.uid}) 정보 성공적으로 업데이트.")
            }
            .addOnFailureListener { e ->
                println("Firestore: 기존 사용자 정보 업데이트 실패: $e")
            }
    }

    suspend fun deleteUser(uid: String) {
        userRef(uid).document().delete().await()
    }
}