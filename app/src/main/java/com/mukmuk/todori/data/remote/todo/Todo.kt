package com.mukmuk.todori.data.remote.todo

import com.google.firebase.Timestamp


data class Todo(
    val todoId: String = "",
    val uid: String = "",
    val title: String = "",
    val categoryId: String = "",
    val description: String? = null,
    val date: String = "",
    val completed: Boolean = false,
    val totalFocusTimeMillis: Long = 0L,
    val createdAt: Timestamp? = null,
)