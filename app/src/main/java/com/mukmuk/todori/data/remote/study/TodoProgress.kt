package com.mukmuk.todori.data.remote.study

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize


@Parcelize
data class TodoProgress(
    val studyTodoId: String = "",
    val studyId: String = "",                 // 해당 study
    val uid: String = "",                     // 스터디원
    val isDone: Boolean = false,              // 일정 수행 여부
    val completedAt: Timestamp? = null        // 수행 시각
) : Parcelable