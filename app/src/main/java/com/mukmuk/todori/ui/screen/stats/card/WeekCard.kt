package com.mukmuk.todori.ui.screen.stats.card

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
import com.mukmuk.todori.ui.screen.stats.DailyRecord
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.White

@Composable
fun WeekCard(record: List<DailyRecord>) {
    val totalStudySeconds = record.sumOf { it.studySeconds }
    val totalCompletedTodos = record.sumOf { it.completedTodos }
    val totalTodos = record.sumOf { it.totalTodos }
    val TodoTotalPer = (totalCompletedTodos.toFloat() / totalTodos * 100).toInt()

    val avgStudyMinutes = totalStudySeconds / 60 / record.size
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
                Text("평균 공부시간", style = AppTextStyle.MypageButtonText)
                Text(
                    "${avgHours}시간 ${avgMinutes}분",
                    style = AppTextStyle.TitleMedium
                )
                Text(
                    "총합 ${totalHours}시간 ${totalMinutes}분",
                    style = AppTextStyle.MypageButtonText
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
                Text("달성률", style = AppTextStyle.MypageButtonText)
                Text(
                    "${TodoTotalPer}%",
                    style = AppTextStyle.TitleMedium
                )
                Text(
                    "$totalCompletedTodos / $totalTodos",
                    style = AppTextStyle.MypageButtonText
                )
            }
        }
    }
}