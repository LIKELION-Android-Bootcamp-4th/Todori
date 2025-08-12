package com.mukmuk.todori.ui.screen.todo.list.goal

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.mukmuk.todori.ui.screen.todo.component.GoalCard
import kotlinx.datetime.LocalDate


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GoalTodoList(selectedDate: LocalDate, navController: NavHostController) {
    val viewModel: GoalTodoListViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()
//    val uid = "testuser"

    val uid = Firebase.auth.currentUser?.uid.toString()

    // 최초 한 번만 데이터 로드
    LaunchedEffect(uid) {
        viewModel.loadGoalsWithTodos(uid)
    }
    LazyColumn {
        items(state.goals) { goal ->
            val todos = state.goalTodosMap[goal.goalId]?.filter { it.dueDate == selectedDate.toString() }.orEmpty()
            GoalCard(
                goal = goal,
                goalTodos = todos,
                onClick = {
                    navController.navigate("goal/detail/${goal.goalId}")
                }
            )
        }
    }
}
