package com.mukmuk.todori.ui.screen.todo.list

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.mukmuk.todori.data.remote.goal.Goal
import com.mukmuk.todori.data.remote.goal.GoalTodo
import com.mukmuk.todori.ui.screen.todo.component.GoalCard


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GoalTodoList(navController: NavHostController) {
    val goals = listOf(
        Goal(
            goalId = "study-kotlin",
            title = "Kotlin 완전 정복",
            startDate = "2025-07-29",
            endDate = "2025-08-31",
            description = "안드로이드 개발 마스터 되기"
        ),
        Goal(
            goalId = "cs-basic",
            title = "CS 기초 다지기",
            startDate = "2025-08-01",
            endDate = "2025-08-25",
            description = "면접 전 기본 CS 완성"
        ),
        Goal(
            goalId = "android-ui",
            title = "안드로이드 UI 마스터",
            startDate = "2025-07-20",
            endDate = "2025-08-07",
            description = "Compose UI에 익숙해지기"
        )
    )

    val goalTodoMap = mapOf(
        "study-kotlin" to listOf(
            GoalTodo(goalTodoId = "1", title = "Kotlin 문법 정리", isCompleted = true),
            GoalTodo(goalTodoId = "2", title = "Coroutine 학습", isCompleted = false),
            GoalTodo(goalTodoId = "3", title = "Flow 공부", isCompleted = true)
        ),
        "cs-basic" to listOf(
            GoalTodo(goalTodoId = "4", title = "운영체제 정리", isCompleted = true),
            GoalTodo(goalTodoId = "5", title = "네트워크 기본", isCompleted = false)
        ),
//        "android-ui" to listOf(
//            GoalTodo(goalTodoId = "6", title = "LazyColumn 연습", isCompleted = false),
//            GoalTodo(goalTodoId = "7", title = "Canvas 그리기", isCompleted = false),
//            GoalTodo(goalTodoId = "8", title = "애니메이션", isCompleted = false)
//        )
    )

    LazyColumn {
        goals.forEach { goal ->
            val todos = goalTodoMap[goal.goalId].orEmpty()
            item {
                GoalCard(
                    goal = goal,
                    goalTodos = todos
                ) {
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("goal", goal)
                    navController.navigate("goal/detail")
                }
            }
        }
    }
}
