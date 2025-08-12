package com.mukmuk.todori.ui.screen.home

enum class PomodoroTimerMode {
    FOCUSED, SHORT_RESTED, LONG_RESTED
}

enum class TimerStatus {
    IDLE,      // 초기 상태, 정지 상태
    RUNNING,   // 타이머 동작 중
    RECORDING  // 기록 버튼 눌러 멈춘 상태
}

data class TimerState(
    val timeLeftInMillis: Long = 0 * 60 * 1000L,
    val totalStudyTimeMills: Long = 0 * 60 * 1000L,
    val totalRecordTimeMills: Long = 0 * 60 * 1000L,
    val status: TimerStatus = TimerStatus.IDLE,
    val pomodoroMode: PomodoroTimerMode = PomodoroTimerMode.FOCUSED,
    val completedFocusCycles: Int = 0,
    val isPomodoroEnabled: Boolean = true,
    val uid: String = ""
)
