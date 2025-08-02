package com.mukmuk.todori.ui.screen.home

enum class PomodoroTimerMode {
    FOCUSED, SHORT_RESTED, LONG_RESTED
}

data class TimerState(
    val timeLeftInMillis: Long = 1 * 60 * 1000L,
    val totalStudyTimeMills: Long = 0 * 60 * 1000L,
    val isRunning: Boolean = false,
    val pomodoroMode: PomodoroTimerMode = PomodoroTimerMode.FOCUSED,
    val completedFocusCycles: Int = 0,
    val isPomodoroEnabled: Boolean = true
)
