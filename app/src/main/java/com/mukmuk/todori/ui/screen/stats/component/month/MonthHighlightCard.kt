package com.mukmuk.todori.ui.screen.stats.component.month

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Star
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
import com.mukmuk.todori.ui.theme.DarkGray
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.GoalPrimary
import com.mukmuk.todori.ui.theme.GoldenHourAccent
import com.mukmuk.todori.ui.theme.LearningAccent
import com.mukmuk.todori.ui.theme.LightOrange
import com.mukmuk.todori.ui.theme.White

@Composable
fun MonthHighlightCard(
    bestDay: String,
    bestDayQuote: String,
    insights: List<String>
) {
    Column(
        modifier = Modifier.padding(horizontal = Dimens.Medium)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = Dimens.CardDefaultRadius,
            colors = CardDefaults.cardColors(containerColor = White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(Dimens.Medium)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = Dimens.Medium)
                ) {
                    Icon(
                        Icons.Outlined.Star,
                        contentDescription = null,
                        tint = GoalPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(Dimens.Tiny))
                    Text(
                        text = "이번 달 하이라이트",
                        style = AppTextStyle.TitleSmall,
                        color = Black
                    )
                }

                Spacer(modifier = Modifier.height(Dimens.Small))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            LightOrange,
                            RoundedCornerShape(Dimens.Small)
                        )
                        .padding(Dimens.Small)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.EmojiEvents,
                                contentDescription = null,
                                tint = LearningAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(Dimens.Nano))
                            Text(
                                text = "가장 보람찬 날",
                                style = AppTextStyle.BodyTinyMedium,
                                color = LearningAccent
                            )
                        }
                        Text(
                            text = bestDay,
                            style = AppTextStyle.BodySmallMedium,
                            color = Black
                        )
                        Text(
                            text = "\"$bestDayQuote\"",
                            style = AppTextStyle.BodyTinyNormal,
                            color = DarkGray
                        )
                    }
                }
                Spacer(modifier = Modifier.height(Dimens.Large))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = Dimens.Medium)
                ) {
                    Icon(
                        Icons.Outlined.Lightbulb,
                        contentDescription = null,
                        tint = GoldenHourAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(Dimens.Tiny))
                    Text(
                        text = "이번 달 인사이트",
                        style = AppTextStyle.TitleSmall,
                        color = Black
                    )
                }

                insights.forEach { insight ->
                    Row(
                        modifier = Modifier.padding(bottom = Dimens.Tiny)
                    ) {
                        Text(
                            text = "• ",
                            style = AppTextStyle.BodySmallNormal,
                            color = DarkGray
                        )
                        Text(
                            text = insight,
                            style = AppTextStyle.BodySmallNormal,
                            color = DarkGray
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(Dimens.Medium))
    }
}