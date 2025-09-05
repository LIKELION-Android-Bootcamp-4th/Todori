package com.mukmuk.todori.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.mukmuk.todori.data.service.TodoStatsService
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
class TodoStatsRepository @Inject constructor(
    private val todoStatsService: TodoStatsService
) {

    suspend fun getCompletedTodoCount(uid: String, year: Int, month: Int): Int {
        val todos = todoStatsService.getTodosByMonth(uid, year, month)
        return todos.count { it.completed }
    }

    suspend fun getTotalTodoCount(uid: String, year: Int, month: Int): Int {
        val todos = todoStatsService.getTodosByMonth(uid, year, month)
        return todos.size
    }
}