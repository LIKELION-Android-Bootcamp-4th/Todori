package com.mukmuk.todori.data.remote.quest
import com.google.firebase.Timestamp


data class DailyUserQuest(
    val questId: String = "",
    val title: String = "",
    val description: String = "",
    val points: Int = 0,
    val completed: Boolean = false,
    val completedAt: Timestamp? = null
)