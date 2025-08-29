package com.mukmuk.todori.ui.screen.stats.component.report

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.ReportPrimary
import com.mukmuk.todori.ui.theme.ReportSecondary

@Composable
fun StudyGoalCard(
    startHour: Int,
    endHour: Int,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.Medium),
        shape = Dimens.CardDefaultRadius,
        colors = CardDefaults.cardColors(containerColor = ReportSecondary)
    ) {
        Column(
            modifier = Modifier
                .padding(Dimens.Large)
                .fillMaxWidth(),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Schedule,
                    contentDescription = null,
                    tint = ReportPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(Dimens.Tiny))
                Text(
                    text = "이번 달, 당신은",
                    style = AppTextStyle.TitleSmall,
                    color = Black
                )
            }
            Spacer(modifier = Modifier.height(Dimens.Large))
            if (startHour == 0 && endHour == 0) {
                Text(
                    text = "아직 충분한 학습 데이터가 없어요.\n조금 더 기록해보세요!",
                    style = AppTextStyle.BodySmallBold,
                    color = ReportPrimary
                )
            } else {
                Text(
                    text = "'${if (startHour == endHour) "${startHour}시" else "${startHour}~${endHour}시"} 집중러' 였어요",
                    style = AppTextStyle.BodySmallNormal,
                    color = ReportPrimary
                )

                Spacer(modifier = Modifier.height(Dimens.Large))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(140.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    HourlyHighlightCircle(
                        hourRange = startHour..endHour,
                        centerText = if (startHour == endHour) "${startHour}시" else "${startHour}~${endHour}시",
                        modifier = Modifier.fillMaxSize(),
                        highlightColor = ReportPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimens.Medium))
        }
    }
}
