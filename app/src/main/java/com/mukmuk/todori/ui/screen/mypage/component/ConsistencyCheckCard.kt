package com.mukmuk.todori.ui.screen.mypage.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Daily
import com.mukmuk.todori.ui.theme.DarkGray
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Dimens.CardDefaultRadius
import com.mukmuk.todori.ui.theme.Warning
import com.mukmuk.todori.ui.theme.White
import kotlin.math.abs
import kotlin.math.max

@Composable
fun SimpleConsistencyCheckCard(
    dailyMinutes: Int,
    weeklyMinutes: Int,
    monthlyMinutes: Int,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dailyHours = dailyMinutes / 60f
    val weeklyHours = weeklyMinutes / 60f
    val monthlyHours = monthlyMinutes / 60f

    val dailyTo30Days = dailyHours * 30
    val weeklyTo4Weeks = weeklyHours * 4

    val tolerance = 0.1f
    val isDailyWeeklyConsistent = if (dailyTo30Days > 0 && weeklyTo4Weeks > 0) {
        abs(dailyTo30Days - weeklyTo4Weeks) / max(dailyTo30Days, weeklyTo4Weeks) <= tolerance
    } else true

    val isDailyMonthlyConsistent = if (dailyTo30Days > 0 && monthlyHours > 0) {
        abs(dailyTo30Days - monthlyHours) / max(dailyTo30Days, monthlyHours) <= tolerance
    } else true

    val isWeeklyMonthlyConsistent = if (weeklyTo4Weeks > 0 && monthlyHours > 0) {
        abs(weeklyTo4Weeks - monthlyHours) / max(weeklyTo4Weeks, monthlyHours) <= tolerance
    } else true

    val isAllConsistent = isDailyWeeklyConsistent && isDailyMonthlyConsistent && isWeeklyMonthlyConsistent

    val backgroundColor = if (isAllConsistent) Daily else Warning

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = CardDefaultRadius,
                ambientColor = backgroundColor.copy(alpha = 0.1f)
            ),
        colors = CardDefaults.cardColors(
            containerColor = White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.None),
        shape = CardDefaultRadius,
    ) {
        Column(
            modifier = Modifier.padding(Dimens.Large)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpanded() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isAllConsistent) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = null,
                        tint = backgroundColor,
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(Dimens.Small))

                    Column {
                        Text(
                            text = "목표 일관성 체크",
                            style = AppTextStyle.BodyLarge
                        )

                        Text(
                            text = if (isAllConsistent) {
                                "목표 간 일관성이 좋아요!"
                            } else {
                                "목표를 다시 확인해보세요"
                            },
                            style = AppTextStyle.BodyTinyNormal.copy(color = DarkGray)
                        )
                    }
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = DarkGray
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier.padding(top = Dimens.Medium)
                ) {
                    if (dailyMinutes > 0) {
                        ConsistencyItem(
                            text = "일일 목표 × 30일",
                            hours = dailyTo30Days,
                            isConsistent = true
                        )
                    }

                    if (weeklyMinutes > 0) {
                        ConsistencyItem(
                            text = "주간 목표 × 4주",
                            hours = weeklyTo4Weeks,
                            isConsistent = if (dailyMinutes > 0) isDailyWeeklyConsistent else true
                        )
                    }

                    if (monthlyMinutes > 0) {
                        ConsistencyItem(
                            text = "월간 목표",
                            hours = monthlyHours,
                            isConsistent = if (dailyMinutes > 0) isDailyMonthlyConsistent
                            else if (weeklyMinutes > 0) isWeeklyMonthlyConsistent
                            else true
                        )
                    }

                    if (!isAllConsistent) {
                        Spacer(modifier = Modifier.height(Dimens.Medium))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "💡",
                                style = AppTextStyle.BodySmallNormal
                            )

                            Spacer(modifier = Modifier.width(Dimens.Tiny))

                            Text(
                                text = "목표 간 일관성을 맞춰보세요. 달성 가능한 목표가 더 효과적이에요!",
                                style = AppTextStyle.BodySmallNormal.copy(color = DarkGray)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ConsistencyItem(
    text: String,
    hours: Float,
    isConsistent: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = AppTextStyle.BodySmallNormal
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = String.format("%.1fh", hours),
                style = AppTextStyle.BodySmallBold,
                modifier = Modifier.padding(end = Dimens.Tiny)
            )

            Text(
                text = if (isConsistent) "✓" else "⚠️",
                style = AppTextStyle.BodySmallNormal
            )
        }
    }
}