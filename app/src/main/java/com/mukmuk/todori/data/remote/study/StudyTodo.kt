package com.mukmuk.todori.data.remote.study

import com.google.firebase.Timestamp


data class StudyTodo(
    val studyTodoId: String = "",             // 문서 ID
    val title: String = "",                   // 해야 할 일
    val createdBy: String = "",               // 해당 TODO를 만든 사람 ID
    val createdAt: Timestamp? = null,         // 생성 시간
    val updatedAt: Timestamp? = null          // 수정 시간
)