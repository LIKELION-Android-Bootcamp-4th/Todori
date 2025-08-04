package com.mukmuk.todori.ui.screen.stats.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.mukmuk.todori.ui.screen.stats.WeeklyData
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.White

@Composable
fun WeekCard() {
    var weekdata = listOf(
        WeeklyData(2025, 7, 3, 542, 70, 100),
        WeeklyData(2025, 7, 4, 512, 60, 100),
        WeeklyData(2025, 8, 1, 432, 30, 100),
    )
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
                    .padding(Dimens.XLarge),
            ) {
                Text("평균 공부시간", style = AppTextStyle.MypageButtonText)
                Text("${weekdata[0].studyMinute/7/60}시간 ${weekdata[0].studyMinute/7%60}분", style = AppTextStyle.TitleMedium)
                Text("총합 ${weekdata[0].studyMinute/60}시간 ${weekdata[0].studyMinute%60}분", style = AppTextStyle.MypageButtonText)
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
                    .padding(Dimens.XLarge),
            ) {
                Text("달성률", style = AppTextStyle.MypageButtonText)
                Text("67%", style = AppTextStyle.TitleMedium)
                Text("${weekdata[0].completedTodo} / ${weekdata[0].todoTotal}", style = AppTextStyle.MypageButtonText)
            }
        }
    }
}