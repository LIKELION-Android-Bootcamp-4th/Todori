package com.mukmuk.todori.ui.screen.mypage

import com.mukmuk.todori.data.remote.user.User

data class ProfileUiState(
    val isLoading: Boolean = false,
    val isUpdating: Boolean = false,
    val isLoggingOut: Boolean = false,
    val isDeleting: Boolean = false,
    val user: User? = null,
    val error: String? = null
)