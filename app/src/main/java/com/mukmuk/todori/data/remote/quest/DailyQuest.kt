package com.mukmuk.todori.data.remote.quest
import com.google.firebase.Timestamp


data class DailyUserQuest(
    val questId: String = "",
    val title: String = "",
    val point: Int = 0,
    val isCompleted: Boolean = false,
    val completedAt: Timestamp? = null
)