package com.mukmuk.todori.ui.screen.home.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.ui.screen.home.PomodoroTimerMode
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.GroupSecondary

@Composable
fun PomoModeTextBox(pomodoroMode: PomodoroTimerMode) {
    Surface(
        shape = RoundedCornerShape(50),
        color = GroupSecondary, 
        contentColor = Color.Black,
    ) {
        if (pomodoroMode == PomodoroTimerMode.FOCUSED) {
            Text(
                text = "집중",
                modifier = Modifier.padding(horizontal = Dimens.Large, vertical = Dimens.Tiny),
                style = AppTextStyle.BodyLarge
            )
        } else if (pomodoroMode == PomodoroTimerMode.SHORT_RESTED) {
            Text(
                text = "짧은 휴식",
                modifier = Modifier.padding(horizontal = Dimens.Large, vertical = Dimens.Tiny),
                style = AppTextStyle.BodyLarge
            )
        } else if (pomodoroMode == PomodoroTimerMode.LONG_RESTED) {
            Text(
                text = "긴 휴식",
                modifier = Modifier.padding(horizontal = Dimens.Large, vertical = Dimens.Tiny),
                style = AppTextStyle.BodyLarge
            )
        } else {
            Text(
                text = "휴식",
                modifier = Modifier.padding(horizontal = Dimens.Large, vertical = Dimens.Tiny),
                style = AppTextStyle.BodyLarge
            )
        }

    }
}