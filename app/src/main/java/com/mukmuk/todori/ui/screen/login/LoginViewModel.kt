package com.mukmuk.todori.ui.screen.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
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
            is LoginEvent.GoogleLogin -> signInWithGoogle(event.idToken)
            is LoginEvent.LoginSuccess -> {
                state = state.copy(status = LoginStatus.SUCCESS, userId = event.userId)
            }
            is LoginEvent.LoginFailure -> {
                state = state.copy(status = LoginStatus.FAILURE, errorMessage = event.errorMessage)
            }
            else -> Unit
        }
    }

    private fun checkAutoLogin() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            state = state.copy(status = LoginStatus.SUCCESS, userId = currentUser.uid)
        }
    }

    private fun signInWithGoogle(idToken: String?) {
        if (idToken == null) {
            onEvent(LoginEvent.LoginFailure("Google ID Token이 없습니다."))
            return
        }

        state = state.copy(status = LoginStatus.LOADING)

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val isNewUser = task.result?.additionalUserInfo?.isNewUser

                    if (user != null) {
                        uploadUserToFirestore(user, isNewUser)
                        onEvent(LoginEvent.LoginSuccess(user.uid))
                    }
                } else {
                    onEvent(LoginEvent.LoginFailure(task.exception?.localizedMessage ?: "로그인 실패"))
                }
            }
    }

    private fun uploadUserToFirestore(user: FirebaseUser, isNewUser: Boolean?) {
        val currentTime = System.currentTimeMillis()
        val userData = User(
            uid = user.uid,
            nickname = user.displayName ?: "",
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
                onEvent(LoginEvent.LoginFailure(e.message ?: "Firestore 저장 실패"))
            }
        }
    }
}