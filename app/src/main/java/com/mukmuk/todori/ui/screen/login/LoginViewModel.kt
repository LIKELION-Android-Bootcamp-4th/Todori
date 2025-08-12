package com.mukmuk.todori.ui.screen.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.mukmuk.todori.R
import com.mukmuk.todori.data.repository.AuthRepository
import com.mukmuk.todori.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    var state by mutableStateOf(LoginState())

    fun getGoogleSignInIntent(context: Context): Intent {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, gso).signInIntent
    }

    fun handleGoogleSignInResult(data: Intent?) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken
            if (!idToken.isNullOrBlank()) {
                onEvent(LoginEvent.GoogleLogin(idToken))
            } else {
                onEvent(LoginEvent.LoginFailure("Google ID Token이 없습니다."))
            }
        } catch (e: ApiException) {
            onEvent(LoginEvent.LoginFailure("Google 로그인 실패: ${e.localizedMessage}"))
        }
    }

    private fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.GoogleLogin -> signInWithGoogle(event.idToken)
            is LoginEvent.LoginFailure -> {
                state = state.copy(status = LoginStatus.ERROR, errorMessage = event.errorMessage)
            }
            else -> Unit
        }
    }

    private fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            state = state.copy(status = LoginStatus.LOADING, errorMessage = null)
            try {
                authRepository.signInWithGoogle(idToken)
                state = state.copy(status = LoginStatus.SUCCESS)
            } catch (e: Exception) {
                state = state.copy(status = LoginStatus.ERROR, errorMessage = e.localizedMessage)
            }
        }
    }

    fun loginWithTestAccount(
        email: String = "test@test.com",
        password: String = "test1234",
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e.message ?: "로그인 실패") }
    }

    fun kakaoLogin(context: Context) {
        viewModelScope.launch {
            state = state.copy(status = LoginStatus.LOADING, errorMessage = null)
            try {
                userRepository.loginWithKakao(context)
                state = state.copy(status = LoginStatus.SUCCESS)
            } catch (e: Exception) {
                state = state.copy(status = LoginStatus.ERROR, errorMessage = e.localizedMessage)
            }
        }
    }


    fun naverLogin(activity: Activity) {
        viewModelScope.launch {
            state = state.copy(status = LoginStatus.LOADING, errorMessage = null)
            try {
                userRepository.loginWithNaver(activity)
                state = state.copy(status = LoginStatus.SUCCESS)
            } catch (e: Exception) {
                state = state.copy(status = LoginStatus.ERROR, errorMessage = e.localizedMessage)
            }
        }
    }


}
