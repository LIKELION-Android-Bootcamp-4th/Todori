package com.mukmuk.todori.ui.screen.todo.component

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.data.remote.study.StudyMember
import com.mukmuk.todori.ui.component.CustomLinearProgressBar
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Gray
import com.mukmuk.todori.ui.theme.GroupPrimary
import com.mukmuk.todori.util.levelIconRes

@Composable
fun MemberProgressRow(
    member: StudyMember,
    completed: Int,
    total: Int,
    progress: Float,
    level: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.Tiny),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(levelIconRes(level)),
            contentDescription = "Level $level",
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(12.dp))
        )
        Spacer(modifier = Modifier.width(Dimens.Tiny))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (member.role == "LEADER") {
                    Icon(
                        Icons.Default.EmojiEvents,
                        tint = Color.Yellow,
                        contentDescription = "Leader",
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(modifier = Modifier.width(Dimens.Nano))
                }
                Text(member.nickname, style = AppTextStyle.Body.copy(fontWeight = FontWeight.Bold))
            }
            Text(member.role, style = AppTextStyle.BodySmall)
            Row(verticalAlignment = Alignment.CenterVertically) {
                CustomLinearProgressBar(
                    progress = progress,
                    progressColor = GroupPrimary,
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    trackColor = Gray
                )
                Spacer(modifier = Modifier.width(Dimens.Tiny))
                Text(
                    "$completed/$total",
                    style = AppTextStyle.BodySmall,
                    modifier = Modifier.width(38.dp),
                )
            }
        }
    }
}
