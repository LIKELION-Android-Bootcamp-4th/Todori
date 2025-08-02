package com.mukmuk.todori.ui.screen.todo.component.card

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.data.remote.goal.Goal
import com.mukmuk.todori.data.remote.goal.GoalTodo
import com.mukmuk.todori.ui.screen.todo.component.CardHeaderSection
import com.mukmuk.todori.ui.screen.todo.component.ProgressWithText
import com.mukmuk.todori.ui.screen.todo.component.TodoItemRow
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Black
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
            CardHeaderSection(title = goal.title, subtitle = goal.description)
            Spacer(modifier = Modifier.height(Dimens.Tiny))

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
                        style = AppTextStyle.BodySmall.copy(fontWeight = FontWeight.Bold, color = White),

                    )
                }

                Spacer(modifier = Modifier.width(Dimens.Tiny))
                Icon(Icons.Default.Adjust, contentDescription = null, tint = statusColor )
                Text(statusText, style = AppTextStyle.BodySmall,modifier = Modifier.padding(Dimens.Nano))
            }

            Spacer(modifier = Modifier.height(Dimens.Tiny))

            ProgressWithText(
                progress = completed / total.toFloat(),
                completed = completed,
                total = total,
                progressColor = GoalPrimary,
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = Dimens.Nano
            )

            Spacer(modifier = Modifier.height(Dimens.Medium))

            // 세부 할 일 일부 표시
            goalTodos.take(3).forEach { todo ->
                TodoItemRow(title = todo.title, isDone = todo.isCompleted)
            }
        }
    }
}
