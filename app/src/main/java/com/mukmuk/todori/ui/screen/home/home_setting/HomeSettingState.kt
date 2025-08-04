package com.mukmuk.todori.ui.screen.home.home_setting

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class HomeSettingState(
    val isPomodoroEnabled: Boolean = true,
    val focusMinutes: Int = 25,
    val focusSeconds: Int = 0,
    val shortRestMinutes: Int = 5,
    val shortRestSeconds: Int = 0,
    val longRestMinutes: Int = 15,
    val longRestSeconds: Int = 0
) : Parcelable
