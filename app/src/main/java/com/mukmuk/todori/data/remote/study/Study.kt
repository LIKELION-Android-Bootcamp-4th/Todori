package com.mukmuk.todori.data.remote.study

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize


@Parcelize
data class Study(
    val studyId: String = "",
    val studyName: String = "",
    val title: String = "",
    val description: String = "",
    val leaderId: String = "",
    val createdAt: Timestamp? = null,
    val activeDays: List<String> = emptyList(),
    val deleted: Boolean = false,
    val status: String? = ""
) : Parcelable