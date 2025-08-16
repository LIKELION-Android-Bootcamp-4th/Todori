package com.mukmuk.todori.ui.screen.mypage.studytargets

data class WeeklyPaceData(
    val weeklyTargetMinutes: Int,               // 이번 주 목표 시간
    val requiredDailyMinutes: Int,         // 오늘까지 달성해야 하는 공부 시간
    val actualCumulativeMinutes: Int,           // 이번 주 누적 공부 시간
    val todayTargetMinutes: Int,                // 오늘 하루 목표 공부 시간
    val todayActualMinutes: Int,                // 오늘 공부 시간
    val daysPassed: Int,                        // 이번주(일요일) 이후 경과한 일수
) {
    val weeklyTargetHours: Float get() = weeklyTargetMinutes / 60f
    val requiredDailyHours: Float get() = requiredDailyMinutes / 60f
    val actualCumulativeHours: Float get() = actualCumulativeMinutes / 60f
    val todayTargetHours: Float get() = todayTargetMinutes / 60f
    val todayActualHours: Float get() = todayActualMinutes / 60f
    val todayRemainHours: Float get() = (todayTargetMinutes - todayActualMinutes) / 60f
    val progress: Float get() = if (todayActualMinutes > 0) todayActualMinutes.toFloat() / todayTargetMinutes else 0f
    val isTodayOnTrack: Boolean get() = todayActualMinutes >= todayTargetMinutes * 0.8
    val isWeeklyOnTrack: Boolean get() = actualCumulativeMinutes >= requiredDailyMinutes * 0.8
}