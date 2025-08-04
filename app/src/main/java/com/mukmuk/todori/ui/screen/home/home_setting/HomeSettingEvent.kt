package com.mukmuk.todori.ui.screen.home.home_setting

sealed class HomeSettingEvent {
    data class SetPomodoroEnabled(val isEnabled: Boolean) : HomeSettingEvent()

    data class SetFocusTime(val minutes: Int, val seconds: Int) : HomeSettingEvent()
    data class SetShortRestTime(val minutes: Int, val seconds: Int) : HomeSettingEvent()
    data class SetLongRestTime(val minutes: Int, val seconds: Int) : HomeSettingEvent()
}
