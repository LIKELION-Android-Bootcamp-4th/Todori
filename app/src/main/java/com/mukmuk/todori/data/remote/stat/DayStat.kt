package com.mukmuk.todori.data.remote.stat

import com.google.firebase.Timestamp


data class DayStat(
    val qualified: Boolean = false,
    val streakCount: Int = 0,
    val updatedAt: Timestamp? = null
)