package com.mukmuk.todori.data.remote.study

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize


@Parcelize
data class StudyTodo(
    val studyTodoId: String = "",             // 문서 ID
    val studyId: String = "",                 // 해당 study
    val title: String = "",                   // 해야 할 일
    val date: String = "",                    // 일정 날짜(yyyy-MM-dd)
    val createdBy: String = "",               // 해당 TODO를 만든 사람 ID
    val createdAt: Timestamp? = null,         // 생성 시간
    val updatedAt: Timestamp? = null          // 수정 시간
) : Parcelable
