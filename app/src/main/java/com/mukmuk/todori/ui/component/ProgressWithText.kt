package com.mukmuk.todori.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.DarkGray
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.LightGray
import com.mukmuk.todori.ui.theme.UserPrimary

@Composable
fun ProgressWithText(
    progress: Float,
    completed: Int,
    total: Int,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = Dimens.Nano,
    progressColor: Color = UserPrimary,
    trackColor: Color = LightGray,
    label: String = "진행률"
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                label,
                style = AppTextStyle.BodySmall.copy(fontWeight = FontWeight.Bold, color = DarkGray)
            )
            Text(
                "$completed / $total",
                style = AppTextStyle.BodySmall.copy(fontWeight = FontWeight.Bold, color = DarkGray)
            )
        }
        Spacer(modifier = Modifier.height(Dimens.Tiny))
        CustomLinearProgressBar(
            progress = progress,
            cornerRadius = cornerRadius,
            progressColor = progressColor,
            trackColor = trackColor,
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.Tiny)
        )
    }
}
