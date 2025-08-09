package com.mukmuk.todori.data.remote.community

import com.google.firebase.Timestamp


data class StudyPost (

    val postId: String = "",
    val studyId: String = "",
    val title: String = "",
    val content: String = "",
    val tags: List<String> = emptyList(),
    var memberCount: Int = 0,
    val createdBy: String = "",
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null,
    val isDeleted: Boolean = false

)