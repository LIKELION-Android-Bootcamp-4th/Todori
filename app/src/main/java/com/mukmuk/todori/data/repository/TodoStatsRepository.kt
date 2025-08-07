package com.mukmuk.todori.data.repository

import com.mukmuk.todori.data.service.TodoStatsService
import javax.inject.Inject

class TodoStatsRepository @Inject constructor(
    private val todoStatsService: TodoStatsService
) {

    suspend fun getTodoCompleteStats(
        uid: String,
        year: Int,
        month: Int
    ): Pair<Int, Int> {
        val todosMonth = todoStatsService.getTodosByMonth(uid, year, month)
        val completed = todosMonth.count { it.completed }
        return Pair(completed, todosMonth.size)
    }
}
