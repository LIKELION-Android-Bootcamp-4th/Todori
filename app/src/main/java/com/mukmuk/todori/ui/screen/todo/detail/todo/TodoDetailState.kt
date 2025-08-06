package com.mukmuk.todori.ui.screen.todo.detail.todo

import com.mukmuk.todori.data.remote.todo.Todo
import com.mukmuk.todori.data.remote.todo.TodoCategory

data class TodoDetailState(
    val isLoading: Boolean = false,
    val category: TodoCategory? = null,
    val todos: List<Todo> = emptyList(),
    val error: String? = null
)
