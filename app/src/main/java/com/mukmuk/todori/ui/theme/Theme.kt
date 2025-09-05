package com.mukmuk.todori.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable


private val LightColorScheme = lightColorScheme(
    primary = UserPrimary,
    onPrimary = White,
    primaryContainer = UserHalf,
    onPrimaryContainer = Black,

    secondary = GoalPrimary,
    onSecondary = Black,

    tertiary = GroupPrimary,
    onTertiary = White,

    background = Background,
    onBackground = Black,

    surface = White,
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