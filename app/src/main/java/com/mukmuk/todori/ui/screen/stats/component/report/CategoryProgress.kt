package com.mukmuk.todori.ui.screen.stats.component.report

data class CategoryProgress(
    val name: String,
    val completionRate: Int,
)

data class WeeklyRate(
    val dayName: String,
    val rate: Int
)