package com.mukmuk.todori.data.remote.stat

import com.google.firebase.Timestamp

data class Stats(
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val updatedAt: Timestamp? = null
)
