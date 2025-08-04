package com.mukmuk.todori.ui.screen.community.components

import java.sql.Timestamp

data class StudyPost (
    val title: String = "",
    val content: String = "",
    val tags: List<String> = emptyList(),
    val members: Int = 0,
    val comments: Int = 0,
    var authorId: String = "",
    val createdAt: Timestamp? = null,
)

