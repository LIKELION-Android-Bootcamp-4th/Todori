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
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.ReportPrimary
import com.mukmuk.todori.ui.theme.ReportSecondary
import com.mukmuk.todori.ui.theme.White

@Composable
fun StudyGoalCard(
    currentTime: String,
    completionRate: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.Medium),
        shape = Dimens.CardDefaultRadius,
        colors = CardDefaults.cardColors(containerColor = ReportSecondary)
    ) {
        Column(
            modifier = Modifier.padding(Dimens.Large),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
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

            Text(
                text = "'밤 10시 집중!'했어요",
                style = AppTextStyle.BodySmallNormal,
                color = ReportPrimary
            )

            Spacer(modifier = Modifier.height(Dimens.Large))

            // 시간 표시 (원형 진행률과 함께)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(120.dp)
            ) {
                CircularProgressIndicator(
                    progress = completionRate / 100f,
                    modifier = Modifier.fillMaxSize(),
                    color = ReportPrimary,
                    strokeWidth = 8.dp,
                    trackColor = Color.White
                )
                Text(
                    text = currentTime,
                    style = AppTextStyle.TitleMedium,
                    color = Black
                )
            }

            Spacer(modifier = Modifier.height(Dimens.Medium))

            Box(
                modifier = Modifier
                    .background(
                        ReportPrimary,
                        RoundedCornerShape(Dimens.Medium)
                    )
                    .padding(horizontal = Dimens.Medium, vertical = Dimens.Tiny)
            ) {
                Text(
                    text = "집중률 $completionRate%",
                    style = AppTextStyle.BodySmallMedium,
                    color = White
                )
            }
        }
    }
}