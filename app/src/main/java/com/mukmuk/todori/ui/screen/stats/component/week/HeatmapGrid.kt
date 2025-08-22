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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

@Composable
fun HeatmapGrid() {
    val days = listOf("월", "화", "수", "목", "금", "토", "일")
    val timeSlots = listOf("6-9", "9-12", "12-15", "15-18", "18-21", "21-24")

    // 샘플 데이터 (실제로는 DailyRecord에서 가져올 데이터)
    val heatmapData = remember {
        days.map { day ->
            timeSlots.map { timeSlot ->
                Random.nextInt(0, 101) // 0-100% 랜덤 값
            }
        }
    }

    Column {
        // 시간대 헤더
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Spacer(modifier = Modifier.width(40.dp)) // 요일 칸 공간
            timeSlots.forEach { timeSlot ->
                Text(
                    text = timeSlot,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 히트맵 그리드
        heatmapData.forEachIndexed { dayIndex, dayData ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 요일 라벨
                Text(
                    text = days[dayIndex],
                    modifier = Modifier.width(30.dp),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.width(10.dp))

                // 히트맵 셀들
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
                            color = if (value > 50) Color.White else Color.Black,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // 범례
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "낮음",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


// 히트맵 색상 생성 함수
@Composable
fun getHeatmapColor(value: Int): Color {
    val alpha = (value / 100f).coerceIn(0f, 1f)
    return MaterialTheme.colorScheme.primary.copy(alpha = 0.1f + (alpha * 0.9f))
}
