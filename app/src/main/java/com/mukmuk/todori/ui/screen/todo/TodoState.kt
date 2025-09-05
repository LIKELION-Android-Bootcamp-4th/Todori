package com.mukmuk.todori.ui.screen.todo

import com.mukmuk.todori.data.remote.todo.Todo
import com.mukmuk.todori.data.remote.todo.TodoCategory

data class TodoState (
    val isLoading: Boolean = false,
    val categories: List<TodoCategory> = emptyList(),
    val todosByCategory: Map<String, List<Todo>> = emptyMap(),
    val setCategoryState: Boolean = false,
    val error: String? = null,

    val sendUrl: String? = null
)