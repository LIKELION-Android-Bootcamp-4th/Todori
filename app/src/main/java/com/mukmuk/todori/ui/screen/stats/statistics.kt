package com.mukmuk.todori.ui.screen.stats

data class WeeklyData(
    val year: Int,
    val month: Int,
    val week: Int,
    val studyMinute: Int,
    val completedTodo: Int, //완료 투두
    val todoTotal: Int //투두 총합
)