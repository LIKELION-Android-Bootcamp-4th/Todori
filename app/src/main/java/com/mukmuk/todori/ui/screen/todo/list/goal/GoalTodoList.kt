package com.mukmuk.todori.ui.screen.todo.list.goal

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.mukmuk.todori.ui.screen.todo.component.GoalCard
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Dimens
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GoalTodoList(selectedDate: LocalDate, navController: NavHostController) {
    val viewModel: GoalTodoListViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
//    val uid = "testuser"

    val uid = Firebase.auth.currentUser?.uid.toString()

    // 최초 한 번만 데이터 로드
    LaunchedEffect(uid) {
        viewModel.loadGoalsWithTodos(uid)
    }

    if (state.goals.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.CheckCircleOutline,
                contentDescription = "Goal Empty",
                modifier = Modifier.size(38.dp),
                tint = Color.Gray
            )
            Spacer(modifier = Modifier.height(Dimens.Tiny))
            Text(
                "생성된 목표가 없습니다",
                style = AppTextStyle.BodyLarge,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(Dimens.Tiny))
            Text(
                "기간을 정해 목표를 만들고 할 일을 등록해 보세요!",
                style = AppTextStyle.BodySmall,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(Dimens.Medium))
        }
    }

    LazyColumn {
        items(state.goals) { goal ->
            val end = java.time.LocalDate.parse(goal.endDate, dateFormatter)
            val todos = state.goalTodosMap[goal.goalId]?.filter { it.dueDate == selectedDate.toString() }.orEmpty()
            if (end.isAfter(selectedDate.toJavaLocalDate())) {
                GoalCard(
                    goal = goal,
                    goalTodos = todos,
                    selectedDate = selectedDate,
                    onClick = {
                        navController.navigate("goal/detail/${goal.goalId}?date=${selectedDate}")
                    }
                )
            }
        }
    }
}
