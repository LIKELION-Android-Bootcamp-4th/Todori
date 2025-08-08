package com.mukmuk.todori.data.remote.community

import com.google.firebase.Timestamp

data class StudyPostComment (

    val commentId: String = "",
    val studyId: String = "",
    val uid: String = "",
    val nickname: String = "",
    val content: String = "",
    val parentCommentId: String? = null,
    val createdAt: Timestamp? = null,

)