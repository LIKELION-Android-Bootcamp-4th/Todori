package com.mukmuk.todori.ui.screen.stats.component

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import com.mukmuk.todori.ui.theme.Dimens.DefaultCornerRadius
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.data.remote.dailyRecord.DailyRecord
import com.mukmuk.todori.data.remote.todo.Todo
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.White

@Composable
fun WeekCard(
    record: List<DailyRecord>,
    allTodos: List<Todo>,
    completedTodos: List<Todo>
) {
    val totalStudySeconds = record.sumOf { it.studyTimeMillis }
    val TodoTotalPer = if (allTodos.isNotEmpty()) {
        (completedTodos.size.toFloat() / allTodos.size * 100).toInt()
    } else 0

    val avgStudyMinutes = if (record.size > 1) {
        totalStudySeconds / 60 / record.size
    } else 0
    val avgHours = avgStudyMinutes / 60
    val avgMinutes = avgStudyMinutes % 60

    val totalStudyMinutes = totalStudySeconds / 60
    val totalHours = totalStudyMinutes / 60
    val totalMinutes = totalStudyMinutes % 60

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.Medium),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        //평균 공부시간
        Card(
            modifier = Modifier
                .weight(1f)
                .height(120.dp),
            shape = RoundedCornerShape(DefaultCornerRadius),
            colors = CardDefaults.cardColors(
                containerColor = White
            ),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Dimens.Medium)
            ) {
                Text("평균 공부시간", style = AppTextStyle.BodyTinyNormal)
                Text(
                    "${avgHours}시간 ${avgMinutes}분",
                    style = AppTextStyle.TitleMedium
                )
                Text(
                    "총합 ${totalHours}시간 ${totalMinutes}분",
                    style = AppTextStyle.BodyTinyNormal
                )
            }
        }

        //달성률
        Card(
            modifier = Modifier
                .weight(1f)
                .height(120.dp),
            shape = RoundedCornerShape(DefaultCornerRadius),
            colors = CardDefaults.cardColors(
                containerColor = White
            ),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Dimens.Medium)
            ) {
                Text("달성률", style = AppTextStyle.BodyTinyNormal)
                Text(
                    "${TodoTotalPer}%",
                    style = AppTextStyle.TitleMedium
                )
                Text(
                    "${completedTodos.size} / ${allTodos.size}",
                    style = AppTextStyle.BodyTinyNormal
                )
            }
            Log.d("WeekCard", "📋 All Todos (${allTodos.size}): ${allTodos.joinToString { it.title }}")
            Log.d("WeekCard", "✅ Completed Todos (${completedTodos.size}): ${completedTodos.joinToString { it.title }}")

        }
    }
}