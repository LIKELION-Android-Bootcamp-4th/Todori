package com.mukmuk.todori.data.remote.study

import com.google.firebase.Timestamp


data class StudyMember(
    val uid: String = "",                 // 멤버 ID
    val studyId: String = "",
    val nickname: String = "",            // 멤버 닉네임
    val role: String = "LEADER | MEMBER ", // 역할
    val joinedAt: Timestamp? = null,      // 참여 시간
)