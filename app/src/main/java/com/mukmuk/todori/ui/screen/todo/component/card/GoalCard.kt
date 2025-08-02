package com.mukmuk.todori.ui.screen.todo.component.card

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.data.remote.goal.Goal
import com.mukmuk.todori.data.remote.goal.GoalTodo
import com.mukmuk.todori.ui.component.CustomLinearProgressBar
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.DarkGray
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Dimens.DefaultCornerRadius
import com.mukmuk.todori.ui.theme.GoalPrimary
import com.mukmuk.todori.ui.theme.Gray
import com.mukmuk.todori.ui.theme.Red
import com.mukmuk.todori.ui.theme.UserPrimary
import com.mukmuk.todori.ui.theme.White
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.todayIn
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GoalCard(
    goal: Goal,
    goalTodos: List<GoalTodo>,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val total = goalTodos.size
    val completed = goalTodos.count { it.isCompleted }
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val end = LocalDate.parse(goal.endDate)
    val dDay = remember(goal.endDate) {
        ChronoUnit.DAYS.between(today.toJavaLocalDate(), end.toJavaLocalDate()).toInt()
    }
    val start = LocalDate.parse(goal.startDate)
    val statusColor = when {
        today < start -> Gray
        dDay < 0 -> UserPrimary
        else -> GoalPrimary
    }
    val statusText = when {
        today < start -> "시작 전"
        dDay < 0 -> "완료"
        else -> "진행 중"
    }

    Card(
        modifier = modifier
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
            // 제목과 아이콘
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(text = goal.title, style = AppTextStyle.Body.copy(fontWeight = FontWeight.Bold))
                    Text(text = goal.description ?: "", style = AppTextStyle.BodySmall)
                }
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
            }

            Spacer(modifier = Modifier.height(Dimens.Nano))

            // 기간 + 상태
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.background(color = White)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.background(color = White).border(1.dp, Gray, RoundedCornerShape(DefaultCornerRadius)).padding(Dimens.Nano)
                ) {
                    Icon(Icons.Outlined.CalendarMonth, contentDescription = null, tint = Black,modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(Dimens.Nano))
                    Text("${goal.startDate} ~ ${goal.endDate}", style = AppTextStyle.BodySmall.copy(fontWeight = FontWeight.Bold))
                }

                Spacer(modifier = Modifier.width(Dimens.Tiny))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (dDay < 10) Red else UserPrimary)
                        .padding(vertical = Dimens.Nano, horizontal = Dimens.Tiny)
                ) {
                    Text(
                        text = if (dDay < 0) "종료" else "D-$dDay",
                        style = AppTextStyle.ButtonText.copy(fontWeight = FontWeight.Bold, color = if (dDay < 10) White else Black),

                    )
                }

                Spacer(modifier = Modifier.width(Dimens.Tiny))
                Icon(Icons.Default.Adjust, contentDescription = null, tint = statusColor )
                Text(statusText, style = AppTextStyle.BodySmall,modifier = Modifier.padding(Dimens.Nano))
            }

            Spacer(modifier = Modifier.height(Dimens.Tiny))

            // 진행률
            Text("진행률 $completed / $total", style = AppTextStyle.BodySmall.copy(color = DarkGray))
            Spacer(modifier = Modifier.height(Dimens.Tiny))
            CustomLinearProgressBar(
                progress = completed / total.toFloat(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.Tiny),
                cornerRadius = Dimens.Nano
            )

            Spacer(modifier = Modifier.height(Dimens.Medium))

            // 세부 할 일 일부 표시
            goalTodos.take(3).forEach { todo ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (todo.isCompleted) Icons.Default.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (todo.isCompleted) UserPrimary else Color.Gray
                    )
                    Spacer(modifier = Modifier.width(Dimens.Tiny))
                    Text(
                        text = todo.title,
                        style = AppTextStyle.BodySmall.copy(
                            color = if (todo.isCompleted) DarkGray else Black,
                            textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                        )
                    )
                }
            }
        }
    }
}
