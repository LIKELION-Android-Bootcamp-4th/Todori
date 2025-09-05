package com.mukmuk.todori.ui.screen.mypage.studytargets

data class WeeklyPaceData(
    val weeklyTargetMinutes: Int,
    val requiredDailyMinutes: Int,
    val actualCumulativeMinutes: Int,
    val todayTargetMinutes: Int,
    val todayActualMinutes: Int,
    val daysPassed: Int,
) {
    val weeklyTargetHours: Float get() = weeklyTargetMinutes / 60f
    val requiredDailyHours: Float get() = requiredDailyMinutes / 60f
    val actualCumulativeHours: Float get() = actualCumulativeMinutes / 60f
    val todayTargetHours: Float get() = todayTargetMinutes / 60f
    val todayActualHours: Float get() = todayActualMinutes / 60f
    val todayRemainHours: Float get() = (todayTargetMinutes - todayActualMinutes) / 60f
    val progress: Float get() = if (todayActualMinutes > 0) todayActualMinutes.toFloat() / todayTargetMinutes else 0f
    val isTodayOnTrack: Boolean get() = todayActualMinutes >= todayTargetMinutes * 0.8
}