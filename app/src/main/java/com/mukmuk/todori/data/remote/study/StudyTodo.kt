package com.mukmuk.todori.data.remote.study

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize


@Parcelize
data class StudyTodo(
    val studyTodoId: String = "",
    val studyId: String = "",
    val title: String = "",
    val date: String = "",
    val createdBy: String = "",
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) : Parcelable
