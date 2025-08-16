package com.mukmuk.todori.data.remote.user

import com.google.firebase.Timestamp

data class StudyTargets(
    val dailyMinutes: Int? = null,
    val weeklyMinutes: Int? = null,
    val monthlyMinutes: Int? = null,
    val updatedAt: Timestamp? = null
)