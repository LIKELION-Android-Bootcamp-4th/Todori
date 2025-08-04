package com.mukmuk.todori.ui.screen.stats.card

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.data.remote.goal.GoalTodo
import com.mukmuk.todori.data.remote.study.StudyTodo
import com.mukmuk.todori.data.remote.study.TodoProgress
import com.mukmuk.todori.ui.component.ProgressWithText
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Dimens.DefaultCornerRadius
import com.mukmuk.todori.ui.theme.GoalPrimary
import com.mukmuk.todori.ui.theme.GroupPrimary
import com.mukmuk.todori.ui.theme.UserPrimary
import com.mukmuk.todori.ui.theme.White

@Composable
fun MonthProgress(
    todos: Int,
    goalTodos: Int,
    studyTodos: Int,
//    myProgressMap: Map<String, TodoProgress>
) {
//    val goalCompleted = goalTodos.count { it.isCompleted }
//    val studyCompleted = studyTodos.count { myProgressMap[it.studyTodoId]?.isDone == true }
//    val goalProgress = if (goalTodos.size > 0) goalCompleted / goalTodos.size.toFloat() else 0f
//    val studyProgress = if (studyTodos.size > 0) studyCompleted / studyTodos.size.toFloat() else 0f

    Column {
        Card(
            modifier = Modifier
                .fillMaxWidth(1f)
                .padding(horizontal = Dimens.Medium),
            shape = RoundedCornerShape(DefaultCornerRadius),
            colors = CardDefaults.cardColors(
                containerColor = White
            ),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(Dimens.Medium)) {
                Text("월간 통계", style = AppTextStyle.TitleSmall)
                Spacer(modifier = Modifier.height(Dimens.XLarge))
                //개인
                ProgressWithText(
                    progress = 80/todos.toFloat(), //임시
                    completed = 80, //임시
                    total = todos,
                    progressColor = UserPrimary,
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = Dimens.Nano,
                    label = "개인")
                Spacer(modifier = Modifier.height(Dimens.Large))

                //목표
                ProgressWithText(
                    progress = 10/goalTodos.toFloat(),
                    completed = 10,
                    total = goalTodos,
                    progressColor = GoalPrimary,
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = Dimens.Nano,
                    label = "목표")
                Spacer(modifier = Modifier.height(Dimens.Large))

                //스터디
                ProgressWithText(
                    progress = 10/studyTodos.toFloat(),
                    completed = 10,
                    total = studyTodos,
                    progressColor = GroupPrimary,
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = Dimens.Nano,
                    label = "스터디")
                Spacer(modifier = Modifier.height(Dimens.Medium))
            }
        }
        Spacer(modifier = Modifier.height(Dimens.Large))
    }
}