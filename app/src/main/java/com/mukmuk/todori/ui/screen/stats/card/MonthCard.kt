package com.mukmuk.todori.ui.screen.stats.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.R
import com.mukmuk.todori.ui.screen.stats.DailyRecord
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Dimens.DefaultCornerRadius
import com.mukmuk.todori.ui.theme.White

@Composable
fun MonthCard(record: List<DailyRecord>) {
    val totalStudySeconds = record.sumOf { it.studySeconds }
    val totalCompletedTodos = record.sumOf { it.completedTodos }
    val totalTodos = record.sumOf { it.totalTodos }
    val TodoTotalPer = if (totalTodos > 0) {
        (totalCompletedTodos.toFloat() / totalTodos * 100).toInt()
    } else 0

    val avgStudyMinutes = totalStudySeconds / 60 / record.size
    val avgHours = avgStudyMinutes / 60
    val avgMinutes = avgStudyMinutes % 60

    val totalStudyMinutes = totalStudySeconds / 60
    val totalHours = totalStudyMinutes / 60
    val totalMinutes = totalStudyMinutes % 60

    val completedGoal = 3

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.Medium),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            //총 공부시간
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
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.icon_alltime),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.height(Dimens.Small))
                    Text("총 공부시간", style = AppTextStyle.MypageButtonText)
                    Text(
                        "${totalHours}시간 ${totalMinutes}분",
                        style = AppTextStyle.TitleMedium
                    )
                }
            }

            //투두 달성률
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
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.icon_check),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.height(Dimens.Small))
                    Text("TODO 달성률", style = AppTextStyle.MypageButtonText)
                    Text(
                        "${TodoTotalPer}%",
                        style = AppTextStyle.TitleMedium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(Dimens.Medium))

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
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.icon_average),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.height(Dimens.Small))
                    Text("평균 공부시간", style = AppTextStyle.MypageButtonText)
                    Text(
                        "${avgHours}시간 ${avgMinutes}분",
                        style = AppTextStyle.TitleMedium
                    )
                }
            }

            //완료 목표
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
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.icon_completed_goal),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.height(Dimens.Small))
                    Text("완료 목표", style = AppTextStyle.MypageButtonText)
                    Text("${completedGoal}개", style = AppTextStyle.TitleMedium)
                }
            }
        }
    }
}