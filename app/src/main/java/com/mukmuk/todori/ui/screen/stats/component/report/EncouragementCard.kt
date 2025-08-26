package com.mukmuk.todori.ui.screen.stats.component.report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.GoalPrimary
import com.mukmuk.todori.ui.theme.ReportPrimary
import com.mukmuk.todori.ui.theme.White

@Composable
fun EncouragementCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.Medium),
        shape = Dimens.CardDefaultRadius,
        colors = CardDefaults.cardColors(
            containerColor = ReportPrimary
        )
    ) {
        Column(
            modifier = Modifier.padding(Dimens.Large),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = GoalPrimary,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.height(Dimens.Medium))

            Text(
                text = "8월도 수고하셨어요!",
                style = AppTextStyle.TitleMedium,
                color = White,
                textAlign = TextAlign.Center
            )

            Text(
                text = "꾸준함이 만들어낸 멋진 성장이에요.\n9월엔 더 큰 도약을 기대할게요! ✨",
                style = AppTextStyle.BodySmallNormal,
                color = White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Dimens.Medium))

            Box(
                modifier = Modifier
                    .background(
                        GoalPrimary,
                        RoundedCornerShape(Dimens.Medium)
                    )
                    .padding(horizontal = Dimens.Large, vertical = Dimens.Small)
            ) {
                Text(
                    text = "다음 달도 파이팅!",
                    style = AppTextStyle.BodyLarge,
                    color = Black
                )
            }
        }
    }
}