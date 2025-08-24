package com.mukmuk.todori.ui.screen.stats.tab.week

import com.mukmuk.todori.data.remote.dailyRecord.DailyRecord
import com.mukmuk.todori.data.remote.todo.Todo
import com.mukmuk.todori.data.remote.user.StudyTargets

data class WeekState(
    val completedTodos: Int = 0,
    val totalTodos: Int = 0,
    val totalStudyTimeMillis: Long = 0L,
    val avgStudyTimeMillis: Long = 0L,
    val todos: List<Todo> = emptyList(),
    val studyTargets: StudyTargets? = null,
    val completedTodoItems: List<Todo> = emptyList(),
    val dailyRecords: List<DailyRecord> = emptyList(),
    val insights: WeekInsightsData = WeekInsightsData()
)

data class WeekInsightsData(
    val productiveDay: String = "",
    val productiveDuration: String = "",
    val completionRate: Int = 0,
    val bestTimeSlot: String = "",
    val bestTimeSlotRate: Int = 0,
    val planAchievement: Int = 0
)