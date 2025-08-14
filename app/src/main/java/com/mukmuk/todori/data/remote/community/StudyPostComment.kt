package com.mukmuk.todori.data.remote.community

import com.google.firebase.Timestamp

data class StudyPostComment (

    val commentId: String = "",
    val postId: String = "",
    val uid: String = "",
    val username: String = "",
    val level: String = "",
    val content: String = "",
    val parentCommentId: String? = null,
    val createdAt: Timestamp? = null,

)