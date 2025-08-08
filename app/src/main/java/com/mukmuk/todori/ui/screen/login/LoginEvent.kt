package com.mukmuk.todori.ui.screen.login

sealed class LoginEvent {
    data class GoogleLogin(val idToken: String?) : LoginEvent()
    data class LoginSuccess(val userId: String) : LoginEvent()
    data class LoginFailure(val errorMessage: String) : LoginEvent()

    object KakaoLogin : LoginEvent()
    object NaverLogin : LoginEvent()
}