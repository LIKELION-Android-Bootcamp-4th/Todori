package com.mukmuk.todori.ui.screen.stats.component.report

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
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
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.GoalPrimary
import com.mukmuk.todori.ui.theme.StudyStreakOrange
import com.mukmuk.todori.ui.theme.White
import kotlin.collections.forEachIndexed

@Composable
fun MonthHeroCard(
    topCategories: List<CategoryProgress>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.Medium),
        shape = Dimens.CardDefaultRadius,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF8E1)
        ),
        border = BorderStroke(2.dp, GoalPrimary)
    ) {
        Column(
            modifier = Modifier.padding(Dimens.Medium)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(GoalPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(Dimens.Tiny))
                Text(
                    text = "이번 달의 주인공",
                    style = AppTextStyle.TitleSmall,
                    color = Black
                )
            }

            Text(
                text = "'자료구조' - 11시간",
                style = AppTextStyle.BodySmallNormal,
                color = StudyStreakOrange
            )

            Spacer(modifier = Modifier.height(Dimens.Medium))

            topCategories.forEachIndexed { index, category ->
                CategoryProgressItem(
                    rank = index + 1,
                    category = category
                )
                if (index < topCategories.size - 1) {
                    Spacer(modifier = Modifier.height(Dimens.Tiny))
                }
            }
        }
    }
}