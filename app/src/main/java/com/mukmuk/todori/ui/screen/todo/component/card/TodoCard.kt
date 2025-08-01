package com.mukmuk.todori.ui.screen.todo.component.card

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.ui.component.CustomLinearProgressBar
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.DarkGray
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Dimens.DefaultCornerRadius
import com.mukmuk.todori.ui.theme.Gray
import com.mukmuk.todori.ui.theme.UserPrimary
import com.mukmuk.todori.ui.theme.White

@Composable
fun TodoCard(
    categoryTitle: String,
    subtitle: String,
    progress: Int,
    total: Int,
    todos: List<Pair<String, Boolean>>,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimens.Tiny)
            .clickable { onClick() },
        shape = RoundedCornerShape(DefaultCornerRadius),
        border = BorderStroke(1.dp, Gray),
        colors = CardDefaults.cardColors(
            containerColor = White
        )
    ) {
        Column(modifier = Modifier.padding(Dimens.Medium)) {
            // 상단 제목
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = categoryTitle, style = AppTextStyle.Body)
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
            }

            Spacer(modifier = Modifier.height(Dimens.Nano))

            // 설명
            Text(text = subtitle, style = AppTextStyle.BodySmall.copy(color= DarkGray))

            Spacer(modifier = Modifier.height(Dimens.Medium))

            // 진행률 텍스트
            Text(text = "진행률  $progress / $total", style = AppTextStyle.BodySmall.copy(color = DarkGray))
            Spacer(modifier = Modifier.height(Dimens.Tiny))
            CustomLinearProgressBar(
                progress = progress / total.toFloat(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.Tiny),
                cornerRadius = Dimens.Nano
            )

            Spacer(modifier = Modifier.height(Dimens.Medium))

            todos.take(3).forEach { (task, isDone) ->
                Row(
                    modifier = Modifier.padding(vertical = Dimens.Nano),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isDone) Icons.Outlined.CheckCircle else Icons.Outlined.Info,
                        contentDescription = null,
                        tint = if (isDone) UserPrimary else DarkGray
                    )
                    Spacer(modifier = Modifier.width(Dimens.Small))
                    Text(
                        text = task,
                        style = AppTextStyle.BodySmall.copy(
                            color = if (isDone) DarkGray else Black,
                            textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None
                        )
                    )
                }
            }
        }
    }
}
