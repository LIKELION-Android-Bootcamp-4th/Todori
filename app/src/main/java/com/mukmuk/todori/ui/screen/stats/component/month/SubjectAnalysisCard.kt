package com.mukmuk.todori.ui.screen.stats.component.month

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
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
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.White

@Composable
fun SubjectAnalysisCard(
    subjects: List<SubjectProgress>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.Medium),
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
                    Icons.Outlined.AutoStories,
                    contentDescription = null,
                    tint = Black,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(Dimens.Tiny))
                Text(
                    text = "주제별 성과 분석",
                    style = AppTextStyle.TitleSmall,
                    color = Black
                )
            }
            if (subjects.isEmpty()) {
                Text(
                    text = "아직 분석할 데이터가 부족해요.\n더 많은 학습 기록을 추가해보세요!",
                    style = AppTextStyle.BodySmallBold,
                    color = Black
                )
            } else {
                subjects.forEach { subject ->
                    SubjectProgressItem(subject = subject)
                    if (subject != subjects.last()) {
                        Spacer(modifier = Modifier.height(Dimens.Medium))
                    }
                }
            }
        }
    }
}