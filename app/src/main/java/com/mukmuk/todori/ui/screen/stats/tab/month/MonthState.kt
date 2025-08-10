package com.mukmuk.todori.ui.screen.stats.tab.month

data class MonthState(
    val completedTodos: Int = 0,
    val totalTodos: Int = 0,
    val completedGoals: Int = 0,
    val totalGoals: Int = 0,
    val completedStudyTodos: Int = 0,
    val totalStudyTodos: Int = 0,
    val totalStudyTimeMillis: Long = 0L,
    val avgStudyTimeMillis: Long = 0L
)