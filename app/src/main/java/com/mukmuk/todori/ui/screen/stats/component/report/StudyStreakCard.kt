package com.mukmuk.todori.ui.screen.stats.component.report

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalFireDepartment
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
import com.mukmuk.todori.ui.theme.LightGray
import com.mukmuk.todori.ui.theme.StudyStreakBackground
import com.mukmuk.todori.ui.theme.StudyStreakOrange
import com.mukmuk.todori.ui.theme.White

@Composable
fun StudyStreakCard(
    streakDays: Int,
    maxStreak: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.Medium),
        shape = Dimens.CardDefaultRadius,
        colors = CardDefaults.cardColors(containerColor = White)
    ) {
        Row(
            modifier = Modifier.padding(Dimens.Medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        StudyStreakBackground,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.LocalFireDepartment,
                    contentDescription = null,
                    tint = StudyStreakOrange,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(Dimens.Medium))

            Column {
                Text(
                    text = "연속 ${streakDays}일 공부",
                    style = AppTextStyle.TitleSmall,
                    color = Black
                )
                Text(
                    text = "최고 기록: ${maxStreak}일",
                    style = AppTextStyle.BodySmallNormal,
                    color = DarkGray
                )

                Spacer(modifier = Modifier.height(Dimens.Tiny))

                Column {
                    LazyRow {
                        items(15) { index ->
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        if (index < streakDays.coerceAtMost(15)) StudyStreakOrange else LightGray,
                                        CircleShape
                                    )
                            )
                            if (index < 14) {
                                Spacer(modifier = Modifier.width(Dimens.Nano))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(Dimens.Nano))

                    LazyRow {
                        items(15) { index ->
                            val dayIndex = index + 15
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        if (dayIndex < streakDays) StudyStreakOrange else LightGray,
                                        CircleShape
                                    )
                            )
                            if (index < 14) {
                                Spacer(modifier = Modifier.width(Dimens.Nano))
                            }
                        }
                    }
                }

            }
        }
    }
}