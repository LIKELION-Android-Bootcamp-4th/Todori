package com.mukmuk.todori.ui.screen.stats.component.report

data class CategoryProgress(
    val name: String,
    val completionRate: Int,
    val timeText: String
)

data class WeeklyRate(
    val dayName: String,
    val rate: Int
)