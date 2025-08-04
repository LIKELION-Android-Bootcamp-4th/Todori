package com.mukmuk.todori.data.remote.todo

import java.security.Timestamp

data class Todo(
    val todoId: String = "",               // 문서ID
    val uid: String = "",                  // 해당 todo의 사용자
    val title: String = "",                // 할 일 제목
    val categoryId: String = "",           // 카테고리
    val description: String? = null,       // 상세 설명
    val date: String = "",                 // "yyyy-MM-dd" 형식
    val isCompleted: Boolean = false,      // 완료 여부
    val totalFocusTimeMillis: Long = 0L,   // 누적 타이머 시간
    val createdAt: Timestamp? = null,      // 생성 시각
    val updatedAt: Timestamp? = null       // 마지막 수정 시각
)