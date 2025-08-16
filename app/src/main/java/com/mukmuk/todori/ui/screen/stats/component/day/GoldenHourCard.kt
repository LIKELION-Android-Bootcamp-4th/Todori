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
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material3.AssistChip
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
import kotlin.math.abs


@Composable
fun GoldenHourCard(
    startHour: Int?,
    windowHours: Int = 2,
    completionDeltaPercent: Int? = null,
    badgeText: String = "집중 최적 시간",
    modifier: Modifier = Modifier,
) {
    val accent = Color(0xFF9D53BB)

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(DefaultCornerRadius),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.fillMaxWidth().padding(Dimens.Medium)) {

            Row {
                Icon(Icons.Outlined.FlashOn, contentDescription = null, tint = accent)
                Spacer(Modifier.width(6.dp))
                Text("골든 아워", style = AppTextStyle.BodyLarge)
            }
            Spacer(Modifier.height(Dimens.Small))

            if (startHour == null) {
                Text("데이터가 더 필요해요", style = AppTextStyle.BodySmall.copy(color = Gray))
                Spacer(Modifier.height(8.dp))
                AssistChip(
                    onClick = {},
                    enabled = false,
                    label = { Text(badgeText) }
                )
                return@Column
            }

            val end = (startHour + windowHours) % 24
            Text(
                text = "%02d:00-%02d:00".format(startHour, end),
                style = AppTextStyle.TitleMedium.copy(color = accent)
            )

            // 완료율 증감 표시
            completionDeltaPercent?.let { pct ->
                Spacer(Modifier.height(4.dp))
                val up = pct >= 0
                val sign = if (up) "+" else "−"
                val color = if (up) accent else Color(0xFFEF4444)
                Text(
                    text = "완료율 $sign${abs(pct)}%",
                    style = AppTextStyle.BodySmall.copy(color = color)
                )
            }

        }
    }
}
