package com.mukmuk.todori.data.remote

data class Quest(
    val questId: String = "",              // 고유 ID
    val title: String = "",                // 예: "타이머로 30분 이상 집중"
    val point: Int = 10,                   // 획득 포인트
)