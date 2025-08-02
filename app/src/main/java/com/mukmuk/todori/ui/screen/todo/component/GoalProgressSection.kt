package com.mukmuk.todori.ui.screen.todo.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mukmuk.todori.data.remote.goal.Goal
import com.mukmuk.todori.data.remote.goal.GoalTodo
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.GoalPrimary

@Composable
fun GoalProgressSection(goal: Goal) {

    val dummyTodos = listOf(
        GoalTodo(title = "계획 세우기", isCompleted = true, dueDate = "2025-08-05"),
        GoalTodo(title = "일정 조율하기", isCompleted = false, dueDate = "2025-08-08")
    )

    val total = dummyTodos.size
    val completed = dummyTodos.count { it.isCompleted }

    ProgressWithText(
        progress = if (total == 0) 0f else completed / total.toFloat(),
        completed = completed,
        total = total,
        progressColor = GoalPrimary,
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = Dimens.Nano
    )
}
