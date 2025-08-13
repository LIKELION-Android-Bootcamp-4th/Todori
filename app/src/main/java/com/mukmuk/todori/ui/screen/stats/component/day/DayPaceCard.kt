package com.mukmuk.todori.ui.screen.stats.component.day

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.ui.component.CustomLinearProgressBar
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Dimens.DefaultCornerRadius
import com.mukmuk.todori.ui.theme.White

@Composable
fun DayPaceCard() {
    val expectedCum = 25
    val actualCum = 18.5
    val delta = expectedCum - actualCum
    val todayTarget = 5
    val todayActual = 4.5
    val todayRemain = todayTarget - todayActual
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
        Column(modifier = Modifier.fillMaxWidth().padding(Dimens.Medium)) {
            //상단 - 상태
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row{
                    Icon(Icons.Outlined.Speed, contentDescription = null, tint = Color.Blue)
                    Spacer(modifier = Modifier.width(Dimens.Small))
                    Text(text = "오늘의 페이스",style = AppTextStyle.BodyLarge)
                }
                Box(
                    modifier = Modifier.background(Color.Red,shape = RoundedCornerShape(Dimens.Tiny))
                        .padding(horizontal = Dimens.Tiny, vertical = Dimens.Nano),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "지연", style = AppTextStyle.BodySmallBold.copy(color = White))
                }
            }
            Spacer(modifier = Modifier.height(Dimens.Small))
            //목표 텍스트
            /**
             * expectedCum = sum(expected) 평소 오늘까지 기대 누적 hour
             * actualCum = sum(actual) 오늘까지 실제 누적 hour
             * delta = autualCum - expectedCum
             */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "이번 주 목표 ${expectedCum}h 중 ${actualCum}h", style = AppTextStyle.BodySmall)
                Text(text = "오늘까지 기대치: ${todayTarget}h",style = AppTextStyle.BodySmall)
            }
            Spacer(modifier = Modifier.height(Dimens.Tiny))
            //프로그레스바
            CustomLinearProgressBar(
                progress = (actualCum / expectedCum).toFloat(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.Medium),
                progressColor = Black
            )
            Spacer(modifier = Modifier.height(Dimens.Tiny))
            //현재 진행률
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "현재: ${todayActual}h", style = AppTextStyle.BodySmall)
                Text(text = "-${todayRemain}h", style = AppTextStyle.BodySmall)
            }
            Spacer(modifier = Modifier.height(Dimens.Tiny))
            //콜아웃 텍스트
            Box(
                modifier = Modifier.fillMaxWidth().background(color = Color(0xfff6f5ff), shape = RoundedCornerShape(Dimens.Tiny))
                    .padding(vertical = Dimens.Tiny, horizontal = Dimens.Small)
            ) {
                Text("오늘 목표 ${todayTarget}h 까지 ${todayRemain}h", style = AppTextStyle.BodySmall)
            }
        }
    }
}