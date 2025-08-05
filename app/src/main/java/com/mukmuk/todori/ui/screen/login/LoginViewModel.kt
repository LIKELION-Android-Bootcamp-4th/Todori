package com.mukmuk.todori.ui.screen.login

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
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

    fun uploadUserToFirestore(user: FirebaseUser) {
        val firestore = FirebaseFirestore.getInstance()
        val currentTime = System.currentTimeMillis()

        val userData = mapOf(
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

        firestore.collection("users")
            .document(user.uid)
            .set(userData)
            .addOnSuccessListener {
                Log.d("Firestore", "User data successfully written!")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error writing user data", e)
            }
    }
}