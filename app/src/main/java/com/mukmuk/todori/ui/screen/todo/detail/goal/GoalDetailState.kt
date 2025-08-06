package com.mukmuk.todori.ui.screen.todo.detail.goal

import com.mukmuk.todori.data.remote.goal.Goal
import com.mukmuk.todori.data.remote.goal.GoalTodo

data class GoalDetailState(
    val isLoading: Boolean = false,
    val goal: Goal? = null,
    val todos: List<GoalTodo> = emptyList(),
    val error: String? = null,
    val goalDeleted: Boolean = false

)
