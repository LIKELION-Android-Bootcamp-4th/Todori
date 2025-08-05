package com.mukmuk.todori.data.remote.dailyRecords

import com.google.firebase.Timestamp

data class DailyRecords(
    val date: String = "",            // "yyyy-MM-dd"
    val uid: String = "",
    val studyTimeMillis: Long = 0L,   // 총 공부 시간 (밀리초)
    val reflection: String? = null,   // 한 줄 회고
    val pomodoroSessionCount: Int? = 0,// 뽀모도로 반복 횟수
    val createdAt: Timestamp? = null
)