package com.mukmuk.todori.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable


private val LightColorScheme = lightColorScheme(
    primary = UserPrimary,               // 주 컬러 (ex. 버튼, 강조 색상)
    onPrimary = White,                   // primary 위에 올 글자색
    primaryContainer = UserHalf,         // 버튼 배경 등
    onPrimaryContainer = Black,

    secondary = GoalPrimary,            // 보조 색상
    onSecondary = Black,

    tertiary = GroupPrimary,            // 서브 보조 색상
    onTertiary = White,

    background = Background,            // 기본 배경색
    onBackground = Black,

    surface = White,                    // 카드, 시트 등 표면 색
    onSurface = Black,
    error = Red,
    onError = White

)

@Composable
fun TodoriTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
      colorScheme = LightColorScheme,
      typography = Typography,
      content = content
    )
}