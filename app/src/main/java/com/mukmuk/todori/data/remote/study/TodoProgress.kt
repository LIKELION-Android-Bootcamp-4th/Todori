package com.mukmuk.todori.data.remote.study

import com.google.firebase.Timestamp


data class TodoProgress(
    val studyTodoId: String = "",
    val uid: String = "",                     // 스터디원
    val isDone: Boolean = false,              // 일정 수행 여부
    val completedAt: Timestamp? = null        // 수행 시각
)