package com.mukmuk.todori.ui.screen.mypage.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.data.remote.quest.DailyUserQuest
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Background
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Gray
import com.mukmuk.todori.ui.theme.LightGray
import com.mukmuk.todori.ui.theme.UserPrimary
import com.mukmuk.todori.ui.theme.UserTenth

@Composable
fun QuestSection(
    quests: List<DailyUserQuest>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Background, shape = RoundedCornerShape(Dimens.DefaultCornerRadius)) // 카드 배경
            .padding(Dimens.Medium)
    ) {
        Text(
            text = "퀘스트",
            style = AppTextStyle.Body,
            modifier = Modifier.padding(bottom = Dimens.Medium)
        )

        Column(verticalArrangement = Arrangement.spacedBy(Dimens.Small)) {
            quests.forEach { quest ->
                QuestItem(quest)
            }
        }
    }
}

@Composable
fun QuestItem(quest: DailyUserQuest) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.ButtonCornerRadius))
            .background(
                if (quest.completed) UserTenth
                else LightGray
            )
            .padding(horizontal = Dimens.Medium, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .padding(start = Dimens.Tiny)
                .size(12.dp)
                .clip(RoundedCornerShape(50))
                .background(
                    if (quest.completed) UserPrimary
                    else Gray
                )
        )

        Spacer(modifier = Modifier.width(Dimens.Medium))

        Text(
            text = "${quest.title} (+${quest.points})",
            style = AppTextStyle.Body,
        )
    }
}
