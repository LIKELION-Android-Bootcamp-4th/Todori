package com.mukmuk.todori.data.remote.stat

import com.google.firebase.Timestamp

data class MonthStat(
    val year: Int = 0,
    val month: Int = 0,
    val totalStudyTime: Long = 0L,
    val totalTodos: Int = 0,
    val completedTodos: Int = 0,
    val todoCompletionRate: Int = 0,
    val categoryStats: List<CategoryStat> = emptyList(),
    val goalStats: List<GoalStat> = emptyList(),
    val bestDay: BestDay? = null,
    val updatedAt: Timestamp? = null
)

data class CategoryStat(
    val categoryId: String = "",
    val name: String = "",
    val completionRate: Int = 0
)

data class GoalStat(
    val goalId: String = "",
    val title: String = "",
    val completionRate: Int = 0
)

data class BestDay(
    val date: String = "",
    val completionRate: Int = 0,
    val studyTime: String = "",
    val score: Int = 0
)
