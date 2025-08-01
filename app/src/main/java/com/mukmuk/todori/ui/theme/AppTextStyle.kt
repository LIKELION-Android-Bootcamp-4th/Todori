package com.mukmuk.todori.ui.theme

import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.White

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object AppTextStyle {

    val Timer = TextStyle(
        fontFamily = NotoSans,
        fontWeight = FontWeight.Bold,
        fontSize = 56.sp
    )

    // 제목
    val TitleLarge = TextStyle(
        fontFamily = NotoSans,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp
    )

    val TitleMedium = TextStyle(
        fontFamily = NotoSans,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 30.sp
    )

    val TitleSmall = TextStyle(
        fontFamily = NotoSans,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 24.sp
    )

    // 앱바
    val AppBar = TextStyle(
        fontFamily = NotoSans,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
    )

    // 본문
    val Body = TextStyle(
        fontFamily = NotoSans,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 22.sp
    )

    val BodyLarge = TextStyle(
        fontFamily = NotoSans,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 22.sp
    )

    val BodyLargeGray = TextStyle(
        fontFamily = NotoSans,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        color = Gray
    )

    val BodySmall = TextStyle(
        fontFamily = NotoSans,
        fontWeight = FontWeight.Light,
        fontSize = 14.sp,
        lineHeight = 20.sp
    )

    // 버튼 텍스트
    val ButtonText = TextStyle(
        fontFamily = NotoSans,
        fontWeight = FontWeight.Thin,
        fontSize = 12.sp,
        lineHeight = 18.sp
    )

    val BodyLargeWhite = TextStyle(
        fontFamily = NotoSans,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 22.sp,
        color = White
    )

    val BodyLargeGrayButton = TextStyle(
        fontFamily = NotoSans,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 22.sp
    )

    // 마이페이지 버튼 텍스트
    val MypageButtonText = TextStyle(
        fontFamily = NotoSans,
        fontWeight = FontWeight.Thin,
        fontSize = 12.sp,
        lineHeight = 18.sp
    )
}
