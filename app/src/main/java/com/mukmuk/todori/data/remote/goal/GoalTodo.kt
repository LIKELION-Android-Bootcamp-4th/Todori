package com.mukmuk.todori.data.remote.goal

import com.google.firebase.Timestamp


data class GoalTodo(
    val goalTodoId: String = "",
    val goalId: String = "",
    val uid: String = "",
    val title: String = "",
    val dueDate: String? = null,
    val isImportant: Boolean = false,
    val totalFocusTimeMillis: Long = 0L,
    val completed: Boolean = false,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)