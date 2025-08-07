package com.mukmuk.todori.ui.screen.todo.list.goal

import com.mukmuk.todori.data.remote.goal.Goal
import com.mukmuk.todori.data.remote.goal.GoalTodo


data class GoalTodoListState(
    val isLoading: Boolean = false,
    val goals: List<Goal> = emptyList(),
    val goalTodosMap: Map<String, List<GoalTodo>> = emptyMap(),
    val error: String? = null
)
