package com.mukmuk.todori.data.remote.dailyRecord

import com.google.firebase.Timestamp

data class DailyRecord(
    val date: String = "",            // "yyyy-MM-dd"
    val uid: String = "",
    val studyTimeMillis: Long = 0L,   // 총 공부 시간 (밀리초)
    val recordTimeMillis: Long? = 0L,
    val reflection: String? = null,   // 한 줄 회고
    val reflectionV2: ReflectionV2? = null,
    val pomodoroSessionCount: Int? = 0,// 뽀모도로 반복 횟수
    val createdAt: Timestamp? = null,
    val hourlyMinutes: Map<String, Long> = emptyMap()
)