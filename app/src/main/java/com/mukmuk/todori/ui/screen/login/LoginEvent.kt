package com.mukmuk.todori.ui.screen.login

sealed class LoginEvent {
    object KakaoLogin : LoginEvent()
    object GoogleLogin : LoginEvent()
    object NaverLogin : LoginEvent()

    data class LoginSuccess(val userId: String) : LoginEvent()
    data class LoginFailure(val errorMessage: String) : LoginEvent()
}