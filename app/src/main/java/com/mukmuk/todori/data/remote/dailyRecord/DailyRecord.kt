package com.mukmuk.todori.data.remote.dailyRecord

import com.google.firebase.Timestamp

data class DailyRecord(
    val date: String = "",
    val uid: String = "",
    val studyTimeMillis: Long = 0L,
    val recordTimeMillis: Long? = 0L,
    val reflection: String? = null,
    val reflectionV2: ReflectionV2? = null,
    val pomodoroSessionCount: Int? = 0,
    val createdAt: Timestamp? = null,
    val hourlyMinutes: Map<String, Long> = emptyMap()
)