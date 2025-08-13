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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Dimens.DefaultCornerRadius
import com.mukmuk.todori.ui.theme.Gray
import com.mukmuk.todori.ui.theme.White

@Composable
fun LearningStreakCard(
    currentStreak: Int?,                   // 연속 일수 (null이면 데이터 없음/로딩)
    bestStreak: Int?,                      // 최고 기록 (null 가능)
    qualifiedToday: Boolean = false,       // 오늘 30분 충족 여부(포인트 색 강조)
    modifier: Modifier = Modifier,
    accentColor: Color = Color(0xFFF09643) // 포인트 컬러
) {
    val accent = if (qualifiedToday) accentColor else Gray

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
                style = AppTextStyle.BodySmall.copy(color = Gray)
            )
        }
    }
}