package com.mukmuk.todori.ui.screen.mypage

sealed interface ProfileEffect {
    data object LoggedOut : ProfileEffect
    data object DeleteSuccess : ProfileEffect
    data object NeedsReauth : ProfileEffect
    data class ShowMessage(val message: String) : ProfileEffect
}