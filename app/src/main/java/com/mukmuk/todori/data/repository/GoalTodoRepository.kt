package com.mukmuk.todori.data.repository

import com.mukmuk.todori.data.remote.goal.GoalTodo
import com.mukmuk.todori.data.service.GoalTodoService
import javax.inject.Inject

class GoalTodoRepository @Inject constructor(
    private val goalTodoService: GoalTodoService
) {
    suspend fun createGoalTodo(uid: String, goalTodo: GoalTodo): GoalTodo {
        return goalTodoService.createGoalTodo(uid, goalTodo)
    }
    suspend fun getGoalTodosByGoalId(uid: String, goalId: String) = goalTodoService.getGoalTodosByGoalId(uid, goalId)
    suspend fun updateGoalTodo(uid: String, goalTodo: GoalTodo) = goalTodoService.updateGoalTodo(uid, goalTodo)
    suspend fun deleteGoalTodo(uid: String, goalTodoId: String) = goalTodoService.deleteGoalTodo(uid, goalTodoId)
}
