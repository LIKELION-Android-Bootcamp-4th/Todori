package com.mukmuk.todori.ui.screen.todo.list.todo

import com.mukmuk.todori.data.remote.todo.Todo
import com.mukmuk.todori.data.remote.todo.TodoCategory
import java.time.LocalDate

data class TodoListState(
    val isLoading: Boolean = false,
    val categories: List<TodoCategory> = emptyList(),
    val todosByCategory: Map<String, List<Todo>> = emptyMap(),
    val sendCategories: List<TodoCategory> = emptyList(),
    val sendTodosByCategory: Map<String, List<Todo>> = emptyMap(),
    val selectedDate: LocalDate? = null,
    val error: String? = null,

    val sendUrl: String? = null
)

