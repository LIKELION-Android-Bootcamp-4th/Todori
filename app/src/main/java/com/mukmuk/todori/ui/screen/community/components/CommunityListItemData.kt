package com.mukmuk.todori.ui.screen.community.components

import com.google.firebase.Timestamp


data class StudyPost (
    val postId: String = "",
    val title: String = "",
    val content: String = "",
    val tags: List<String> = emptyList(),
    var authorId: String = "",
    val createdAt: Timestamp? = null,
)

