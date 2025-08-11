package com.mukmuk.todori.ui.screen.login

enum class LoginStatus {
    IDLE, LOADING, SUCCESS, ERROR
}

data class LoginState(
    val status: LoginStatus = LoginStatus.IDLE,
    val userId: String? = null,
    val errorMessage: String? = null
)