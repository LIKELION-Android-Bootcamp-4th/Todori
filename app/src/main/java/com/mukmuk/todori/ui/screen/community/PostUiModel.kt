package com.mukmuk.todori.ui.screen.community

import com.google.firebase.Timestamp

data class PostUiModel(
    val postId: String,
    val studyId: String,
    val title: String,
    val content: String,
    val userId: String,
    val nickname: String,
    val level: Int,
    val tags: List<String>,
    val memberCount: Int,
    val commentsCount: Int,
    val createdAt: Timestamp?,
    val updatedAt: Timestamp?,
)
