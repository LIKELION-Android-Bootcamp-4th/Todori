package com.mukmuk.todori.ui.screen.community.components

import java.sql.Timestamp

data class CommentList (
    val userName: String,
    val content: String,
    val createdAt: Timestamp? = null
)