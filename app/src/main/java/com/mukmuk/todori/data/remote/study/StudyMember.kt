package com.mukmuk.todori.data.remote.study

import com.google.firebase.Timestamp


data class StudyMember(
    val uid: String = "",
    val studyId: String = "",
    val nickname: String = "",
    val role: String = "LEADER | MEMBER ",
    val joinedAt: Timestamp? = null,
)