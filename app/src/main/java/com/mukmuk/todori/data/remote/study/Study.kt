package com.mukmuk.todori.data.remote.study

import com.google.firebase.Timestamp


data class Study(
    val studyId: String = "",         // study Id
    val studyName: String = "",       // study 명
    val title: String = "",           // study 설명용 제목
    val description: String = "",     // study 설명
    val leaderId: String = "",        // 스터디장 uid
    val createdAt: Timestamp? = null, // 생성시간
    val activeDays: List<String> = emptyList(), //스터디 활동일
    val isDeleted: Boolean = false,   // 스터디 삭제 여부
    val status: String? = ""     // "PENDING", "ENDED", "ACTIVE" 스터디 상태
)