package com.mukmuk.todori.ui.screen.todo.create

import com.mukmuk.todori.data.remote.todo.TodoCategory

data class TodoCategoryUiState(
    val isLoading: Boolean = false,
    val categories: List<TodoCategory> = emptyList(),
    val error: String? = null,
    val success: Boolean = false
)
