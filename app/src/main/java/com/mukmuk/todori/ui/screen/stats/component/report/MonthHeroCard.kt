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
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.GoalPrimary
import com.mukmuk.todori.ui.theme.LightYellow
import com.mukmuk.todori.ui.theme.StudyStreakOrange
import com.mukmuk.todori.ui.theme.White

@Composable
fun MonthHeroCard(
    topCategories: List<CategoryProgress>
) {
    val topCategory = topCategories.firstOrNull()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.Medium),
        shape = Dimens.CardDefaultRadius,
        colors = CardDefaults.cardColors(
            containerColor = LightYellow
        ),
        border = BorderStroke(1.dp, GoalPrimary)
    ) {
        Column(
            modifier = Modifier.padding(Dimens.Medium)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(GoalPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.EmojiEvents,
                        contentDescription = null,
                        tint = White,
                        modifier = Modifier.size(25.dp)
                    )
                }
                Spacer(modifier = Modifier.width(Dimens.Tiny))
                Column {
                    Text(
                        text = "이번 달의 주인공",
                        style = AppTextStyle.TitleSmall,
                        color = Black
                    )
                    if (topCategory != null) {
                        Text(
                            text = "'${topCategory.name}' - ${topCategory.completionRate}%",
                            style = AppTextStyle.BodySmallNormal,
                            color = StudyStreakOrange,
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    } else {
                        Text(
                            text = "데이터가 부족해 통계를 내기 어려워요.",
                            style = AppTextStyle.BodySmallBold,
                            color = Black.copy(alpha = 0.6f),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }


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