package com.mukmuk.todori.ui.screen.stats.tab.report

import android.os.Build
import androidx.annotation.RequiresApi
import com.mukmuk.todori.ui.screen.stats.component.report.CategoryProgress

@RequiresApi(Build.VERSION_CODES.O)
data class MonthlyReportState(
    val isLoading: Boolean = false,
    val totalStudyTimeHour: Int = 0,
    val targetMonthStudyHour: Int = 0,
    val bestDay: String? = null,
    val bestDayStudyTime: String? = null,
    val goldenHourRange: Pair<Int, Int>? = null,
    val goldenHourText: String? = null,
    val streakDays: Int = 0,
    val maxStreak: Int = 0,
    val currentTodoCompletionRate: Int = 0,
    val lastTodoCompletionRate: Int = 0,
    val previousAvgStudyMinutes: Int = 0,
    val currentAvgStudyMinutes: Int = 0,
    val categoryStats: List<CategoryProgress> = emptyList(),
    val error: String? = null
) {
    val improvement
        get() = currentTodoCompletionRate - lastTodoCompletionRate
    val enduranceImprovement: Int
        get() = currentAvgStudyMinutes - previousAvgStudyMinutes
    val leftTime: Int
        get() = targetMonthStudyHour - totalStudyTimeHour
}
