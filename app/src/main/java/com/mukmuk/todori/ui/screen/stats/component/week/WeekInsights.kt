package com.mukmuk.todori.ui.screen.stats.component.week

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.ui.screen.stats.tab.week.WeekInsightsData
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Dimens.CardDefaultRadius
import com.mukmuk.todori.ui.theme.UserPrimary
import com.mukmuk.todori.ui.theme.White

@Composable
fun WeekInsights(insights: WeekInsightsData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimens.Large),
        shape = CardDefaultRadius,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(White)
    ) {
        Column(
            modifier = Modifier.padding(Dimens.Medium)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = Dimens.Medium)
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = UserPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(Dimens.Tiny))
                Text(
                    text = "이번 주 인사이트",
                    style = AppTextStyle.Body
                )
            }
            var hasInsight = false
            if (insights.productiveDay.isNotBlank() && insights.completionRate > 0) {
                hasInsight = true
                Text(
                    "${insights.productiveDay}이 가장 생산적이었어요 (${insights.productiveDuration}, ${insights.completionRate}% 완료율)",
                    style = AppTextStyle.BodySmall
                )
            }

            if (insights.bestTimeSlot.isNotBlank() && insights.bestTimeSlotRate > 0) {
                hasInsight = true
                Text(
                    "${insights.bestTimeSlot} 시간대에 평균 ${insights.bestTimeSlotRate}% 집중도를 보였어요",
                    style = AppTextStyle.BodySmall
                )
            }

            if (insights.planAchievement > 0) {
                hasInsight = true
                Text(
                    "계획 대비 실제 학습시간이 ${insights.planAchievement}% 달성되었어요",
                    style = AppTextStyle.BodySmall
                )
            }
            if (!hasInsight) {
                Text(
                    "아직 인사이트를 도출할 데이터가 부족해요.\n더 많은 학습 기록을 남겨보세요!",
                    style = AppTextStyle.BodySmallBold
                )
            }
        }
    }
}