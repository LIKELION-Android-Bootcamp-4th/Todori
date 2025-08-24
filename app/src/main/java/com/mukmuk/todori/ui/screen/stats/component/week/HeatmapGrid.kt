package com.mukmuk.todori.ui.screen.stats.component.week

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.UserPrimary

@Composable
fun HeatmapGrid(heatmapData: List<List<Int>>) {
    val days = listOf("일", "월", "화", "수", "목", "금", "토")
    val timeSlots = listOf("6-9", "9-12", "12-15", "15-18", "18-21", "21-24")

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Spacer(modifier = Modifier.width(40.dp))
            timeSlots.forEach { timeSlot ->
                Text(
                    text = timeSlot,
                    modifier = Modifier.weight(1f),
                    style = AppTextStyle.BodyTinyNormal.copy(textAlign = TextAlign.Center)
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimens.Tiny))

        heatmapData.forEachIndexed { dayIndex, dayData ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = days[dayIndex],
                    modifier = Modifier.width(30.dp),
                    style = AppTextStyle.BodySmall
                )

                Spacer(modifier = Modifier.width(Dimens.Tiny))

                dayData.forEach { value ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(32.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(getHeatmapColor(value)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${value}%",
                            style = AppTextStyle.heatmapText.copy(
                                color = if (value > 50) Color.White else Color.Black,
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(Dimens.Small))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.Small),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "낮음",
                style = AppTextStyle.BodyTinyMedium
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                repeat(5) { index ->
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(getHeatmapColor(index * 25))
                    )
                }
            }

            Text(
                text = "높음",
                style = AppTextStyle.BodyTinyMedium
            )
        }
    }
}


@Composable
fun getHeatmapColor(value: Int): Color {
    val alpha = (value / 100f).coerceIn(0f, 1f)
    return UserPrimary.copy(alpha = 0.1f + (alpha * 0.9f))
}
