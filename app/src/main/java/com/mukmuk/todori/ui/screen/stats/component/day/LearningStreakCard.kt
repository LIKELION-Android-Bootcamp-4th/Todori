package com.mukmuk.todori.ui.screen.stats.component.day

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.DarkGray
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Dimens.DefaultCornerRadius
import com.mukmuk.todori.ui.theme.Gray
import com.mukmuk.todori.ui.theme.LearningAccent
import com.mukmuk.todori.ui.theme.White

@Composable
fun LearningStreakCard(
    currentStreak: Int?,
    bestStreak: Int?,
    qualifiedToday: Boolean = false,
    modifier: Modifier = Modifier
) {
    val accent = if (qualifiedToday) LearningAccent else Gray

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(DefaultCornerRadius),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.Medium)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Icon(
                    Icons.Filled.LocalFireDepartment,
                    contentDescription = null,
                    tint = accent
                )
                Spacer(Modifier.width(Dimens.Small))
                Text("연속 학습", style = AppTextStyle.BodyLarge)
            }
            Spacer(modifier = Modifier.height(Dimens.Small))

            Text(
                text = currentStreak?.let { "${it}일" } ?: "— 일",
                style = AppTextStyle.TitleMedium.copy(color = accent)
            )

            Spacer(modifier = Modifier.height(Dimens.Tiny))

            Text(
                text = "최고 기록: ${bestStreak ?: "—"}일",
                style = AppTextStyle.BodySmall.copy(color = DarkGray)
            )
        }
    }
}