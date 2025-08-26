package com.mukmuk.todori.ui.screen.stats.component.report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.DarkGray
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.ProgressTeal
import com.mukmuk.todori.ui.theme.White

@Composable
fun EnduranceCard(
    previousAverage: Int,
    currentAverage: Int,
    improvement: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.Medium),
        shape = Dimens.CardDefaultRadius,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE6FFFA)
        )
    ) {
        Column(
            modifier = Modifier.padding(Dimens.Medium)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.ShowChart,
                    contentDescription = null,
                    tint = ProgressTeal,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(Dimens.Tiny))
                Text(
                    text = "지구력이 늘었어요!",
                    style = AppTextStyle.TitleSmall,
                    color = Black
                )
            }

            Text(
                text = "세션당 +${improvement}분",
                style = AppTextStyle.BodySmallNormal,
                color = ProgressTeal
            )

            Spacer(modifier = Modifier.height(Dimens.Medium))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${previousAverage}분",
                        style = AppTextStyle.TitleMedium,
                        color = DarkGray
                    )
                    Text(
                        text = "지난달 평균",
                        style = AppTextStyle.BodyTinyNormal,
                        color = DarkGray
                    )
                }

                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = ProgressTeal
                )

                Column {
                    Text(
                        text = "${currentAverage}분",
                        style = AppTextStyle.TitleMedium,
                        color = ProgressTeal
                    )
                    Text(
                        text = "이번달 평균",
                        style = AppTextStyle.BodyTinyNormal,
                        color = DarkGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimens.Medium))

            Box(
                modifier = Modifier
                    .background(
                        ProgressTeal,
                        RoundedCornerShape(Dimens.Medium)
                    )
                    .padding(horizontal = Dimens.Medium, vertical = Dimens.Tiny)
            ) {
                Text(
                    text = "집중력 +${improvement}분",
                    style = AppTextStyle.BodySmallMedium,
                    color = White
                )
            }
        }
    }
}