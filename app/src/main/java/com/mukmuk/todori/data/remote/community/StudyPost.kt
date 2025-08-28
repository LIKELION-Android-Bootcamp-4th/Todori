package com.mukmuk.todori.data.remote.community

import com.google.firebase.Timestamp


data class StudyPost (
    val postId: String = "",
    val studyId: String = "",
    val title: String = "",
    val content: String = "",
    val uid: String = "",
    val level: Int = 0,
    val tags: List<String> = emptyList(),
    val memberCount: Int = 0,
    val commentsCount: Int = 0,
    val createdBy: String = "",
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null,
    val deleted: Boolean = false
)