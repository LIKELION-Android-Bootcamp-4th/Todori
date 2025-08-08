package com.mukmuk.todori.ui.screen.login

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.mukmuk.todori.data.remote.user.User
import com.mukmuk.todori.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    var state by mutableStateOf(LoginState())
        private set

    init {
        checkAutoLogin()
    }

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

    private fun checkAutoLogin() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            state = state.copy(
                status = LoginStatus.SUCCESS,
                userId = currentUser.uid
            )
        }
    }

    fun uploadUserToFirestore(user: FirebaseUser, isNewUser: Boolean?) {
        val currentTime = System.currentTimeMillis()
        val userData = User(
            uid = user.uid,
            nickname = user.displayName!!,
            createdAt = currentTime,
            lastLoginAt = currentTime
        )

        viewModelScope.launch {
            try {
                if (isNewUser == true) {
                    repository.createUser(user.uid, userData)
                } else {
                    repository.updateUser(user.uid, userData)
                }
            } catch (e: Exception) {
                LoginEvent.LoginFailure("${e.message}")
            }
        }
    }
}