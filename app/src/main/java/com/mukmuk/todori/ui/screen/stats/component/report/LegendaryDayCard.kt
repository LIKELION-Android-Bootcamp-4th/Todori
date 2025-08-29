package com.mukmuk.todori.ui.screen.stats.component.report

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.GoalPrimary
import com.mukmuk.todori.ui.theme.ReportPrimary
import com.mukmuk.todori.ui.theme.White

@Composable
fun LegendaryDayCard(
    date: String?,
    studyTime: String?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.Medium),
        shape = Dimens.CardDefaultRadius,
        colors = CardDefaults.cardColors(
            containerColor = ReportPrimary
        )
    ) {
        val hasData = !date.isNullOrBlank() && !studyTime.isNullOrBlank()
        Column(
            modifier = Modifier.fillMaxWidth().padding(Dimens.Large),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    Icons.Outlined.StarOutline,
                    contentDescription = null,
                    tint = GoalPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(Dimens.Tiny))
                Text(
                    text = "전설의 하루",
                    style = AppTextStyle.TitleSmall,
                    color = White
                )
            }

            if (hasData) {
                Text(
                    text = date,
                    style = AppTextStyle.Timer.copy(fontSize = 48.sp),
                    color = White
                )
                Text(
                    text = studyTime,
                    style = AppTextStyle.TitleSmall,
                    color = White
                )
                Text(
                    text = "이 날은 정말 전설이었어요",
                    style = AppTextStyle.BodySmallNormal,
                    color = White.copy(alpha = 0.8f)
                )
            } else {
                Spacer(modifier = Modifier.size(Dimens.Medium))
                Text(
                    text = "아직 전설의 하루가 없어요.\n꾸준히 학습 기록을 쌓아보세요!",
                    style = AppTextStyle.BodySmallBold,
                    color = White.copy(alpha = 0.9f)
                )
            }
        }
    }
}