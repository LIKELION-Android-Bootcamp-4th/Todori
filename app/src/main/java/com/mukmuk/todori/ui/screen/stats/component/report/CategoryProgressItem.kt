package com.mukmuk.todori.ui.screen.stats.component.report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.ui.component.CustomLinearProgressBar
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.DarkGray
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.GoalPrimary
import com.mukmuk.todori.ui.theme.Gray
import com.mukmuk.todori.ui.theme.ProgressBackground
import com.mukmuk.todori.ui.theme.StudyStreakOrange
import com.mukmuk.todori.ui.theme.White

@Composable
fun CategoryProgressItem(
    rank: Int,
    category: CategoryProgress
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(
                    when (rank) {
                        1 -> GoalPrimary
                        2 -> Gray.copy(alpha = 0.8f)
                        else -> StudyStreakOrange
                    },
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = rank.toString(),
                style = AppTextStyle.BodySmallMedium,
                color = White
            )
        }

        Spacer(modifier = Modifier.width(Dimens.Tiny))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = category.name,
                style = AppTextStyle.BodySmallMedium,
                color = Black
            )
            CustomLinearProgressBar(
                progress = category.completionRate / 100f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                trackColor = ProgressBackground,
                progressColor = when (rank) {
                    1 -> GoalPrimary
                    2 -> Gray.copy(alpha = 0.8f)
                    else -> StudyStreakOrange
                }
            )
        }

        Text(
            text = "${category.completionRate}%",
            style = AppTextStyle.BodyTinyMedium,
            color = DarkGray
        )
    }
}