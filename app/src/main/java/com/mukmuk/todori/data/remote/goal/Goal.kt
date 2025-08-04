package com.mukmuk.todori.data.remote.goal
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.security.Timestamp

@Parcelize
data class Goal(
    val goalId: String = "",              // 문서 ID
    val uid: String = "",                 // 사용자 ID
    val title: String = "",               // 목표 제목 (예: "Kotlin 정복")
    val description: String? = null,      // 목표 설명
    val color: String? = null,            // 목표 색상 테마 (UI 구분용, hex 형식)
    val isPinned: Boolean = false,        // 상단 고정 여부
    val startDate: String = "",           // 시작일 (yyyy-MM-dd)
    val endDate: String = "",             // 종료일 (yyyy-MM-dd)
    val isCompleted: Boolean = false,     // 전체 목표 완료 여부
    val createdAt: Timestamp? = null,     // 생성 시간
    val updatedAt: Timestamp? = null      // 수정 시간
) : Parcelable