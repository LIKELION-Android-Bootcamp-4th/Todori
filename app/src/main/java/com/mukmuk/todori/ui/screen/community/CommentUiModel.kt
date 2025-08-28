package com.mukmuk.todori.ui.screen.community

import com.google.firebase.Timestamp

data class CommentUiModel(
    val commentId: String,
    val postId: String,
    val uid: String,
    val nickname: String,
    val level: Int,
    val content: String,
    val createdAt: Timestamp?
)