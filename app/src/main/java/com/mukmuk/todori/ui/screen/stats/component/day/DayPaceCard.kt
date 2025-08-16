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
import com.mukmuk.todori.ui.screen.mypage.studytargets.WeeklyPaceData
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Danger
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Dimens.DefaultCornerRadius
import com.mukmuk.todori.ui.theme.Success
import com.mukmuk.todori.ui.theme.White

@Composable
fun DayPaceCard(
    paceData: WeeklyPaceData,
    modifier: Modifier = Modifier
) {
    val statusColor = if (paceData.isTodayOnTrack) Success else Danger
    val statusText = if (paceData.isTodayOnTrack) "정상" else "지연"

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.Medium),
        shape = RoundedCornerShape(DefaultCornerRadius),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.Medium)
        ) {
            // 1. 상단 상태 헤더
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.Speed,
                        contentDescription = null,
                        tint = Color.Blue
                    )
                    Spacer(modifier = Modifier.width(Dimens.Small))
                    Text(
                        text = "오늘의 페이스",
                        style = AppTextStyle.BodyLarge
                    )
                }
                Box(
                    modifier = Modifier
                        .background(
                            statusColor,
                            shape = RoundedCornerShape(Dimens.Tiny)
                        )
                        .padding(horizontal = Dimens.Tiny, vertical = Dimens.Nano),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = statusText,
                        style = AppTextStyle.BodySmallBold.copy(color = White)
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimens.Small))

            // 2. 목표 / 기대치 텍스트
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "이번 주 ${String.format("%.1f", paceData.weeklyTargetHours)}h 중 " +
                            "${String.format("%.1f", paceData.actualCumulativeHours)}h",
                    style = AppTextStyle.BodySmall
                )
                Text(
                    text = "기대치: ${String.format("%.1f", paceData.requiredDailyHours)}h",
                    style = AppTextStyle.BodySmall
                )
            }

            Spacer(modifier = Modifier.height(Dimens.Tiny))

            // 3. 진행도 프로그레스바
            CustomLinearProgressBar(
                progress = paceData.progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.Medium),
                progressColor = if (paceData.isTodayOnTrack) Color(0xFF4CAF50) else Color(0xFFF44336)
            )

            Spacer(modifier = Modifier.height(Dimens.Tiny))

            // 4. 오늘 공부 시간 & 잔여 시간
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "오늘: ${String.format("%.1f", paceData.todayActualHours)}h",
                    style = AppTextStyle.BodySmall
                )

                val remainText = if (paceData.todayRemainHours > 0) {
                    "${String.format("%.1f", paceData.todayRemainHours)}h"
                } else {
                    "+${String.format("%.1f", -paceData.todayRemainHours)}h"
                }

                Text(
                    text = remainText,
                    style = AppTextStyle.BodySmall,
                    color = if (paceData.todayRemainHours > 0) Success else Danger
                )
            }

            Spacer(modifier = Modifier.height(Dimens.Tiny))

            // 5. 콜아웃 메시지
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFF6F5FF),
                        shape = RoundedCornerShape(Dimens.Tiny)
                    )
                    .padding(vertical = Dimens.Tiny, horizontal = Dimens.Small)
            ) {
                val calloutText = if (paceData.todayRemainHours > 0) {
                    "오늘 목표 ${String.format("%.1f", paceData.todayTargetHours)}h 까지 " +
                            "${String.format("%.1f", paceData.todayRemainHours)}h 남았어요"
                } else {
                    "오늘 목표를 달성했어요! 🎉"
                }
                Text(
                    text = calloutText,
                    style = AppTextStyle.BodySmall
                )
            }
        }
    }
}
