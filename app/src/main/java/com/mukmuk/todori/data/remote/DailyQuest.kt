package com.mukmuk.todori.data.remote


data class DailyUserQuest(
    val questId: String = "",
    val title: String = "",
    val point: Int = 0,
    val isCompleted: Boolean = false,
    //val completedAt: Timestamp? = null
)