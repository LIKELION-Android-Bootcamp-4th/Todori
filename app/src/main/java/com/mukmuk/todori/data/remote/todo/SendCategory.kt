package com.mukmuk.todori.data.remote.todo

data class SendCategory(
    val sendCategoryId: String = "",
    val users: List<String> = emptyList(),
    val categoryId: String = ""
)