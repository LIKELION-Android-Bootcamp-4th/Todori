package com.mukmuk.todori.ui.screen.community.components

import com.google.firebase.Timestamp

data class CommentList (
    val commentId: String = "",
    val postId: String = "",
    val uid: String = "",
    val userName: String = "",
    val content: String = "",
    val parentCommentId: String? = null,
    val createdAt: Timestamp? = null
)