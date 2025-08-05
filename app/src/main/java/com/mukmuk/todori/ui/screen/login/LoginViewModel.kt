package com.mukmuk.todori.ui.screen.login

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.mukmuk.todori.data.remote.user.User

class LoginViewModel  : ViewModel() {
    var state by mutableStateOf(LoginState())
        private set

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.KakaoLogin -> {
                state = state.copy(status = LoginStatus.LOADING)
            }
            is LoginEvent.GoogleLogin -> {
                state = state.copy(status = LoginStatus.LOADING)
            }
            is LoginEvent.NaverLogin -> {
                state = state.copy(status = LoginStatus.LOADING)
            }
            is LoginEvent.LoginSuccess -> {
                state = state.copy(status = LoginStatus.SUCCESS, userId = event.userId)
            }
            is LoginEvent.LoginFailure -> {
                state = state.copy(status = LoginStatus.FAILURE, errorMessage = event.errorMessage)
            }
        }
    }

    fun uploadUserToFirestore(user: FirebaseUser, isNewUser: Boolean?) {
        val firestore = FirebaseFirestore.getInstance()
        val userDocRef = firestore.collection("users").document(user.uid)
        val currentTime = System.currentTimeMillis()

        if (isNewUser == true) {
            val newUserData = mapOf(
                "uid" to user.uid,
                "nickname" to user.displayName,
                "intro" to null,
                "level" to 1,
                "rewardPoint" to 0,
                "authProvider" to "google",
                "createdAt" to currentTime,
                "lastLoginAt" to currentTime,
                "isDeleted" to false,
                "fcmToken" to null
            )

            userDocRef.set(newUserData)
                .addOnSuccessListener {
                    println("Firestore: 새로운 사용자(${user.uid}) 정보 성공적으로 생성.")
                }
                .addOnFailureListener { e ->
                    println("Firestore: 새로운 사용자 정보 생성 실패: $e")
                }
        } else {
            val updateData = mapOf(
                "lastLoginAt" to currentTime,

            )

            userDocRef.set(updateData, SetOptions.merge())
                .addOnSuccessListener {
                    println("Firestore: 기존 사용자(${user.uid}) 정보 성공적으로 업데이트.")
                }
                .addOnFailureListener { e ->
                    println("Firestore: 기존 사용자 정보 업데이트 실패: $e")
                }
        }
    }
}