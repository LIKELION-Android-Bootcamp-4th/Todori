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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
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
import com.mukmuk.todori.ui.theme.CompletionGreen
import com.mukmuk.todori.ui.theme.CompletionRed
import com.mukmuk.todori.ui.theme.DarkGray
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.LightGreen
import com.mukmuk.todori.ui.theme.LightRed
import com.mukmuk.todori.ui.theme.White

@Composable
fun CompletionRateCard(
    previousRate: Int,
    currentRate: Int,
    improvement: Int
) {
    val isUp = improvement >= 0
    val trendColor = if (isUp) CompletionGreen else CompletionRed
    val trendText = if (isUp) "완료율 상승!" else "완료율 하락..."
    val sign = if (isUp) "+" else ""
    val icon = if (isUp) Icons.AutoMirrored.Filled.TrendingUp else
        Icons.AutoMirrored.Filled.TrendingDown

    val previousRateColor = if (isUp) DarkGray else CompletionRed
    val currentRateColor = if (isUp) CompletionGreen else DarkGray


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.Medium),
        shape = Dimens.CardDefaultRadius,
        colors = CardDefaults.cardColors(
            containerColor = if (isUp) LightGreen else LightRed
        )
    ) {
        Column(
            modifier = Modifier.padding(Dimens.Medium)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = trendColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(Dimens.Tiny))
                Column {
                    Text(
                        text = trendText,
                        style = AppTextStyle.TitleSmall,
                        color = Black
                    )
                    Text(
                        text = "$sign$improvement% ${if (isUp) "향상" else "감소"}",
                        style = AppTextStyle.BodySmallNormal,
                        color = trendColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimens.Medium))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "$previousRate%",
                        style = AppTextStyle.TitleMedium,
                        color = previousRateColor
                    )
                    Text(
                        text = "지난달",
                        style = AppTextStyle.BodyTinyNormal,
                        color = DarkGray
                    )
                }

                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = trendColor
                )

                Column {
                    Text(
                        text = "$currentRate%",
                        style = AppTextStyle.TitleMedium,
                        color = currentRateColor
                    )
                    Text(
                        text = "이번달",
                        style = AppTextStyle.BodyTinyNormal,
                        color = DarkGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimens.Medium))

            Box(
                modifier = Modifier
                    .background(
                        trendColor,
                        RoundedCornerShape(Dimens.Medium)
                    )
                    .padding(horizontal = Dimens.Medium, vertical = Dimens.Tiny)
            ) {
                Text(
                    text = if (isUp) "성장률 +$improvement%" else "감소율 $improvement%",
                    style = AppTextStyle.BodySmallMedium,
                    color = White
                )
            }
        }
    }
}
