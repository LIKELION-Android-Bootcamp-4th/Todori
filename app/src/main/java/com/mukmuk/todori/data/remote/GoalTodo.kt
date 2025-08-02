package com.mukmuk.todori.data.remote

import java.security.Timestamp

data class GoalTodo(
    val goalTodoId: String = "",           // 자동 생성 ID
    val title: String = "",                // 세부 작업명
    val dueDate: String? = null,           // 마감일 (선택)
    val isImportant: Boolean = false,      // 중요 표시 여부 -> String으로 여러 개도 가능
    val totalFocusTimeMillis: Long = 0L,   // 해당 GoalTodo에 쓴 총 공부 시간
    val isCompleted: Boolean = false,      // 완료 여부
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)