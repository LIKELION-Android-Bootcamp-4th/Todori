package com.mukmuk.todori.ui.screen.todo.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.data.remote.goal.Goal
import com.mukmuk.todori.data.remote.goal.GoalTodo
import com.mukmuk.todori.ui.component.ProgressWithText
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Dimens.DefaultCornerRadius
import com.mukmuk.todori.ui.theme.GoalPrimary
import com.mukmuk.todori.ui.theme.Gray
import com.mukmuk.todori.ui.theme.White
import kotlinx.datetime.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GoalCard(
    goal: Goal,
    goalTodos: List<GoalTodo>,
    modifier: Modifier = Modifier,
    selectedDate: LocalDate,
    onClick: () -> Unit
) {
    val total = goalTodos.size
    val completed = goalTodos.count { it.completed }
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
            GoalMetaInfoRow(goal, selectedDate)
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

            goalTodos.take(3).forEach { todo ->
                TodoItemRow(title = todo.title, isDone = todo.completed)
            }
        }
    }
}
