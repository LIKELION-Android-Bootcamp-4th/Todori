package com.mukmuk.todori.ui.screen.home.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.mukmuk.todori.ui.screen.home.PomodoroTimerMode
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Gray
import com.mukmuk.todori.ui.theme.GroupSecondary

@Composable
fun PomoModeTextBox(pomodoroMode: PomodoroTimerMode) {
    if (pomodoroMode == PomodoroTimerMode.FOCUSED) {
        Surface(
            shape = RoundedCornerShape(50),
            color = GroupSecondary,
            contentColor = Color.Black,
        ) {
            Text(
                text = "집중",
                modifier = Modifier.padding(horizontal = Dimens.Large, vertical = Dimens.Tiny),
                style = AppTextStyle.BodyLarge
            )
        }
    } else {
        Surface(
            shape = RoundedCornerShape(50),
            color = Gray,
            contentColor = Color.Black,
        ) {
            when (pomodoroMode) {
                PomodoroTimerMode.SHORT_RESTED -> {
                    Text(
                        text = "짧은 휴식",
                        modifier = Modifier.padding(horizontal = Dimens.Large, vertical = Dimens.Tiny),
                        style = AppTextStyle.BodyLarge
                    )
                }
                PomodoroTimerMode.LONG_RESTED -> {
                    Text(
                        text = "긴 휴식",
                        modifier = Modifier.padding(horizontal = Dimens.Large, vertical = Dimens.Tiny),
                        style = AppTextStyle.BodyLarge
                    )
                }
                else -> {}
            }
        }
    }
}