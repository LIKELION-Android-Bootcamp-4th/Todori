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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
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
import com.mukmuk.todori.ui.theme.LightGray
import com.mukmuk.todori.ui.theme.WeeklyBlue
import com.mukmuk.todori.ui.theme.White

@Composable
fun WeeklyCompletionCard(
    weeklyRates: List<WeeklyRate>
) {
    val maxRate = weeklyRates.maxByOrNull { it.rate }?.rate ?: 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.Medium),
        shape = Dimens.CardDefaultRadius,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFEDE9FE)
        )
    ) {
        Column(
            modifier = Modifier.padding(Dimens.Medium)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = null,
                    tint = WeeklyBlue,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(Dimens.Tiny))
                Text(
                    text = "요일별 완료율 최고",
                    style = AppTextStyle.TitleSmall,
                    color = Black
                )
            }

            Text(
                text = "평균 대비 +32%",
                style = AppTextStyle.BodySmallNormal,
                color = WeeklyBlue
            )

            Spacer(modifier = Modifier.height(Dimens.Medium))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(Dimens.Tiny)
            ) {
                items(weeklyRates) { weeklyRate ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = weeklyRate.dayName,
                            style = AppTextStyle.BodyTinyNormal,
                            color = DarkGray
                        )

                        Box(
                            modifier = Modifier
                                .width(32.dp)
                                .height(40.dp)
                                .background(
                                    if (weeklyRate.rate == maxRate) WeeklyBlue else LightGray,
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(vertical = Dimens.Nano),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${weeklyRate.rate}%",
                                style = AppTextStyle.BodyTinyMedium,
                                color = if (weeklyRate.rate == maxRate) White else DarkGray
                            )
                        }
                    }
                }
            }
        }
    }
}