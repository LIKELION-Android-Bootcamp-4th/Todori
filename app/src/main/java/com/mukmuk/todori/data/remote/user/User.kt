package com.mukmuk.todori.data.remote.user

import com.google.firebase.Timestamp

data class User(
    val uid: String = "",
    val nickname: String = "",
    val intro: String? = null,
    val level: Int = 1,
    val rewardPoint: Int = 0,
    val authProvider: String = "",
    val createdAt: Timestamp? = null,
    val lastLoginAt: Timestamp? = null,
    val isDeleted: Boolean = false,
    val fcmToken: String? = null
)