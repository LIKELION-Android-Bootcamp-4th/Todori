package com.mukmuk.todori.data.remote.study

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize


@Parcelize
data class MyStudy(
    val studyId: String = "",
    val studyName: String = "",
    val description: String = "",
    val activeDays: List<String> = emptyList(),
    val joinedAt: Timestamp? = null,
    val role: String = "MEMBER",
    val nickname: String = "",
    val status: String = "ACTIVE",
    val hasPosted: Boolean = false
) : Parcelable
