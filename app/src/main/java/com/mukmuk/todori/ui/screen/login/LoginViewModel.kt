package com.mukmuk.todori.ui.screen.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class LoginViewModel  : ViewModel() {
    var state by mutableStateOf(LoginState())
        private set

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.KakaoLogin -> {
                state = state.copy(status = LoginStatus.LOADING)
                kakaoLogin()
            }
            is LoginEvent.GoogleLogin -> {
                state = state.copy(status = LoginStatus.LOADING)
                googleLogin()
            }
            is LoginEvent.NaverLogin -> {
                state = state.copy(status = LoginStatus.LOADING)
                naverLogin()
            }
            is LoginEvent.LoginSuccess -> {
                state = state.copy(status = LoginStatus.SUCCESS, userId = event.userId)
            }
            is LoginEvent.LoginFailure -> {
                state = state.copy(status = LoginStatus.FAILURE, errorMessage = event.errorMessage)
            }
        }
    }

    private fun kakaoLogin() {

    }
    private fun googleLogin() {

    }
    private fun naverLogin() {

    }
}