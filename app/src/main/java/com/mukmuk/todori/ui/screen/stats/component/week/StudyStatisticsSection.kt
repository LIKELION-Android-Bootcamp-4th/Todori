package com.mukmuk.todori.ui.screen.stats.component.week

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.mukmuk.todori.data.remote.dailyRecord.DailyRecord
import com.mukmuk.todori.data.remote.todo.Todo
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Dimens.CardDefaultRadius
import com.mukmuk.todori.ui.theme.White
import com.mukmuk.todori.ui.theme.firstGradient
import com.mukmuk.todori.ui.theme.secondGradient

@Composable
fun StudyStatisticsSection(
    record: List<DailyRecord> = emptyList(),
    allTodos: List<Todo> = emptyList(),
    completedTodos: List<Todo> = emptyList()
) {
    val totalStudyMillis = record.sumOf { it.studyTimeMillis }
    val studiedCount = record.count { it.studyTimeMillis > 0L }

    val avgStudyMinutes = if (studiedCount > 0) {
        (totalStudyMillis / 1000 / 60 / studiedCount).toInt()
    } else 0

    val avgHours = avgStudyMinutes / 60
    val avgMinutes = avgStudyMinutes % 60

    val totalStudyMinutes = (totalStudyMillis / 1000 / 60).toInt()
    val totalHours = totalStudyMinutes / 60
    val totalMinutes = totalStudyMinutes % 60

    val todoTotalPer = if (allTodos.isNotEmpty()) {
        (completedTodos.size.toFloat() / allTodos.size * 100).toInt()
    } else 0

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(600.dp) // 전체 높이 확보
            .background(White)
    ) {
        // 🔹 상단 Gradient 배경
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(firstGradient, secondGradient)
                    ),
                    shape = RoundedCornerShape(
                        topStart = Dimens.Medium,
                        topEnd = Dimens.Medium
                    )
                )
        )

        // 🔹 내용
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.Large)
        ) {
            // 상단 통계
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(Dimens.Tiny))
                Text(text = "집중 시간", style = AppTextStyle.BodyLargeWhite)
            }

            Spacer(modifier = Modifier.height(Dimens.Medium))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem("Total", "${totalHours}h ${totalMinutes}m")
                StatItem("Avg", "${avgHours}h ${avgMinutes}m")
                StatItem("Rate", "${todoTotalPer}%")
            }
        }

        // 🔹 카드 2개 (Gradient 아래로 겹치게 배치)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = 130.dp) // 여기서 겹치게 내림
                .padding(horizontal = Dimens.Medium)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                shape = CardDefaultRadius,
                colors = CardDefaults.cardColors(White),
                elevation = CardDefaults.cardElevation(defaultElevation = Dimens.Tiny)
            ) {
                Column(Modifier.padding(Dimens.Small)) {
                    AndroidView(
                        factory = { context -> LineChart(context).apply { setupLineChart(this) } },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimens.Medium))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                shape = CardDefaultRadius,
                colors = CardDefaults.cardColors(White),
                elevation = CardDefaults.cardElevation(defaultElevation = Dimens.Tiny)
            ) {
                Column(Modifier.padding(Dimens.Small)) {
                    AndroidView(
                        factory = { context -> BarChart(context).apply { setupBarChart(this) } },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
