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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.CombinedChart
import com.mukmuk.todori.data.remote.dailyRecord.DailyRecord
import com.mukmuk.todori.data.remote.todo.Todo

@Composable
fun StudyStatisticsSection(
    record: List<DailyRecord> = emptyList(),
    allTodos: List<Todo> = emptyList(),
    completedTodos: List<Todo> = emptyList()
) {
    // WeekCard 로직 재사용
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

    // 달성률
    val todoTotalPer = if (allTodos.isNotEmpty()) {
        (completedTodos.size.toFloat() / allTodos.size * 100).toInt()
    } else 0


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(450.dp)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF4CAF50), // 진한 초록
                            Color(0xFF81C784)  // 연한 초록
                        )
                    ),
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = 0.dp,
                        bottomEnd = 0.dp
                    )
                )
                .clip(RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = 0.dp,
                    bottomEnd = 0.dp
                ))
        )

        // 상단 통계 정보
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // 제목
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "집중 시간",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            // 통계 정보
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    label = "Total",
                    value = "${totalHours}h ${totalMinutes}m"
                )
                StatItem(
                    label = "Avg",
                    value = "${avgHours}h ${avgMinutes}m"
                )
                StatItem(
                    label = "달성률",
                    value = "${todoTotalPer}%"
                )

            }
        }

        // 차트 (하단에 겹쳐서 배치)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .padding(horizontal = 16.dp)
                .offset(y = 130.dp), // 상단 통계 아래로 위치
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            AndroidView(
                factory = { context ->
                    CombinedChart(context).apply {
                        setupCombinedChart(this)
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            )
        }
    }
}
