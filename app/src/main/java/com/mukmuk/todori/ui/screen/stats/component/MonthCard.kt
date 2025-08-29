package com.mukmuk.todori.ui.screen.stats.component

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
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
import com.mukmuk.todori.data.remote.stat.WeekStat
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Dimens.DefaultCornerRadius
import com.mukmuk.todori.ui.theme.Red
import com.mukmuk.todori.ui.theme.UserPrimary
import com.mukmuk.todori.ui.theme.White
import com.mukmuk.todori.util.formatHoursOrDash

@Composable
fun MonthCard(
    completedTodos: Int,
    totalTodos: Int,
    avgStudyTimeMillis: Long,
    totalStudyTimeMillis: Long,
    bestWeek: WeekStat?,
    worstWeek: WeekStat?
) {
    val todoTotalPer = if (totalTodos > 0) {
        (completedTodos.toFloat() / totalTodos * 100).toInt()
    } else 0

    val totalStudyMinutes = (totalStudyTimeMillis / 1000 / 60).toInt()
    val totalHours = totalStudyMinutes / 60
    val totalMinutes = totalStudyMinutes % 60

    val avgStudyMinutes = (avgStudyTimeMillis / 1000 / 60).toInt()
    val avgHours = avgStudyMinutes / 60
    val avgMinutes = avgStudyMinutes % 60

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.Medium),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(120.dp),
                shape = RoundedCornerShape(DefaultCornerRadius),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
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
                    Spacer(modifier = Modifier.height(Dimens.Nano))
                    Text(
                        "평균 ${avgHours}시간 ${avgMinutes}분",
                        style = AppTextStyle.BodyTinyNormal
                    )
                }
            }

            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(120.dp),
                shape = RoundedCornerShape(DefaultCornerRadius),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.icon_check),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.height(Dimens.Small))
                    Text("TODO 달성률", style = AppTextStyle.MypageButtonText)
                    Text("${todoTotalPer}%", style = AppTextStyle.TitleMedium)
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
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(120.dp),
                shape = RoundedCornerShape(DefaultCornerRadius),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row {
                        Icon(
                            Icons.Default.ArrowUpward,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = UserPrimary
                        )
                        Text("베스트 주", style = AppTextStyle.MypageButtonText.copy(color = UserPrimary))
                    }
                    Text(bestWeek?.label ?: "-", style = AppTextStyle.BodySmallMedium)
                    Text(
                        bestWeek?.let { formatHoursOrDash(it.totalStudyTimeMillis) } ?: "-",
                        style = AppTextStyle.TitleMedium.copy(color = UserPrimary)
                    )
                }
            }

            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(120.dp),
                shape = RoundedCornerShape(DefaultCornerRadius),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row {
                        Icon(
                            Icons.Default.ArrowDownward,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = Red
                        )
                        Text("워스트 주", style = AppTextStyle.MypageButtonText.copy(color = Red))
                    }
                    Text(worstWeek?.label ?: "-", style = AppTextStyle.BodySmallMedium)
                    Text(
                        worstWeek?.let { formatHoursOrDash(it.totalStudyTimeMillis) } ?: "-",
                        style = AppTextStyle.TitleMedium.copy(color = Red)
                    )
                }
            }
        }
    }
}