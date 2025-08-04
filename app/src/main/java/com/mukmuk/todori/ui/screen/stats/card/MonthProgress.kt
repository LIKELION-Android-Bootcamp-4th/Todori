package com.mukmuk.todori.ui.screen.stats.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Dimens.DefaultCornerRadius
import com.mukmuk.todori.ui.theme.White

@Composable
fun MonthProgress() {
    Column {
        Card(
            modifier = Modifier
                .fillMaxWidth(1f)
                .padding(horizontal = Dimens.Medium),
            shape = RoundedCornerShape(DefaultCornerRadius),
            colors = CardDefaults.cardColors(
                containerColor = White
            ),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(Dimens.Medium)) {
                Text("월간 통계", style = AppTextStyle.TitleSmall)
                Spacer(modifier = Modifier.height(Dimens.XLarge))
                //개인
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("개인", style = AppTextStyle.BodyLarge)
                    Text("180/210", style = AppTextStyle.Body)
                }
                Spacer(modifier = Modifier.height(Dimens.Tiny))
                Text("프로그레스바..") //나중에 합치기


                Spacer(modifier = Modifier.height(Dimens.Large))
                //목표
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("목표", style = AppTextStyle.BodyLarge)
                    Text("8/10", style = AppTextStyle.Body)
                }
                Spacer(modifier = Modifier.height(Dimens.Tiny))
                Text("프로그레스바..") //나중에 합치기


                Spacer(modifier = Modifier.height(Dimens.Large))
                //스터디
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("스터디", style = AppTextStyle.BodyLarge)
                    Text("80/110", style = AppTextStyle.Body)
                }
                Spacer(modifier = Modifier.height(Dimens.Tiny))
                Text("프로그레스바..") //나중에 합치기
            }
        }
    }
}