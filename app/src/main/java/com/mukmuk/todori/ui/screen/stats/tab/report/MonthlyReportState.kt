package com.mukmuk.todori.ui.screen.stats.tab.report

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
data class MonthlyReportState(
    val year: Int = LocalDate.now().year,
    val month: Int = LocalDate.now().monthValue,
    val isLoading: Boolean = false,

    val totalStudyTimeMillis: Long = 0L,
    val avgStudyTimeMillis: Long = 0L,

    val completedTodos: Int = 0,
    val totalTodos: Int = 0,

    val bestDay: String? = null,
    val bestDayStudyTime: Long = 0L,

    val bestWeekLabel: String? = null,
    val bestWeekStudyTime: Long = 0L,

    val goldenHourRange: Pair<Int, Int>? = null,
    val goldenHourText: String? = null,

    val insights: List<String> = emptyList(),
    val error: String? = null
)
