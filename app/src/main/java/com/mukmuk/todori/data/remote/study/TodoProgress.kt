package com.mukmuk.todori.data.remote.study

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize


@Parcelize
data class TodoProgress(
    val studyTodoId: String = "",
    val studyId: String = "",
    val uid: String = "",
    val done: Boolean = false,
    val date: String = "",
    val completedAt: Timestamp? = null
) : Parcelable