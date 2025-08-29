package com.mukmuk.todori.ui.screen.stats.component.report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.CircleTrackColor
import com.mukmuk.todori.ui.theme.DarkGray
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.GoalPurple
import com.mukmuk.todori.ui.theme.White

@Composable
fun GoalProgressCard(
    currentTime: Int?,
    goalTime: Int?,
    leftTime: Int?
) {
    val hasData = currentTime != null && goalTime != null && goalTime > 0 && leftTime != null

    if (!hasData) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.Medium),
            shape = Dimens.CardDefaultRadius,
            colors = CardDefaults.cardColors(containerColor = White)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.Large),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "이번 달 학습 목표 데이터가 부족해요.\n조금 더 기록해 주세요!",
                    style = AppTextStyle.BodySmallBold,
                    color = DarkGray
                )
            }
        }
    } else {
        val safeGoalTime = if (goalTime!! > 0) goalTime else 1
        val title = when {
            currentTime!! >= goalTime -> "축하해요! 목표를 달성했어요 🎉"
            leftTime!! <= 0 -> "축하해요! 목표를 초과 달성했어요 🔥"
            leftTime in 1..5 -> "목표까지 정말 코앞이에요!"
            leftTime in 6..10 -> "조금만 더 화이팅 💪"
            leftTime in 11..20 -> "꾸준히 하면 이번 달도 성공!"
            leftTime > 20 -> "목표까지 아직 ${leftTime}시간 남았어요!"
            else -> "목표에 도전 중이에요 🚀"
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.Medium),
            shape = Dimens.CardDefaultRadius,
            colors = CardDefaults.cardColors(
                containerColor = White
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.Large),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = GoalPurple,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(Dimens.Tiny))
                    Text(
                        text = title,
                        style = AppTextStyle.TitleSmall,
                        color = Black
                    )
                }
                if (leftTime > 0) {
                    Text(
                        text = "${leftTime}시간만 더!",
                        style = AppTextStyle.BodySmallNormal,
                        color = GoalPurple
                    )
                }
                Spacer(modifier = Modifier.height(Dimens.Large))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(120.dp)
                ) {
                    CircularProgressIndicator(
                        progress = currentTime.toFloat() / safeGoalTime,
                        modifier = Modifier.fillMaxSize(),
                        color = GoalPurple,
                        strokeWidth = 10.dp,
                        trackColor = CircleTrackColor
                    )
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${currentTime}h",
                            style = AppTextStyle.TitleMedium,
                            color = GoalPurple
                        )
                        Text(
                            text = "/ ${goalTime}h",
                            style = AppTextStyle.BodySmallNormal,
                            color = DarkGray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.Medium))

                Box(
                    modifier = Modifier
                        .background(
                            GoalPurple,
                            RoundedCornerShape(Dimens.Medium)
                        )
                        .padding(horizontal = Dimens.Medium, vertical = Dimens.Tiny)
                ) {
                    Text(
                        text = "${(currentTime.toFloat() / goalTime * 100).toInt()}% 달성",
                        style = AppTextStyle.BodySmallMedium,
                        color = White
                    )
                }
            }
        }
    }
}
