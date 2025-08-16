package com.mukmuk.todori.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

object Dimens {
    val None = 0.dp
    val Nano = 4.dp //통계 글자 간격
    val Tiny = 8.dp //통계 글자 간격, 리스트 내 제목-내용 간격
    val Small = 10.dp //제목-내용 간격, 버튼 가로간격, 태그 간격 등
    val Medium = 16.dp //기본 여백(투두리스트 간격 등)
    val Large = 20.dp  //카드 리스트 간격, 버튼 세로간격 등
    val XLarge = 28.dp //투두화면 프로그레스바-투두리스트 간격
    val XXLarge = 40.dp //버튼-제목 간격 등 큰 여백
    val DefaultCornerRadius = 10.dp //텍스트필드, 카드, 투두 탭바
    val ButtonCornerRadius = 32.dp //투두 등 완전 동그란것들

    val CardDefaultRadius = RoundedCornerShape(20.dp)
}