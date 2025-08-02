package com.mukmuk.todori.ui.screen.todo.component

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.mukmuk.todori.data.remote.todo.Todo
import com.mukmuk.todori.data.remote.todo.TodoCategory
import com.mukmuk.todori.ui.screen.todo.component.card.TodoCard
import kotlinx.datetime.LocalDate

@Composable
fun TodoList(selectedDate: LocalDate, navController: NavHostController) {
    val todoCategories = listOf(
        TodoCategory(
            categoryId = "cat1",
            name = "운동",
            description = "몸짱이 될거야 ~~~~~~~~~",
            colorHex = "#22B282"
        ),
        TodoCategory(
            categoryId = "cat2",
            name = "공부",
            description = "오늘도 열공한다~",
            colorHex = "#B28222"
        ),
        TodoCategory(
            categoryId = "cat3",
            name = "명상",
            description = "마음의 평화...",
            colorHex = "#8222B2"
        )
    )

    val todos = listOf(
        listOf(
            Todo(title = "스트레칭 하기", isCompleted = true),
            Todo(title = "스쿼트 50개", isCompleted = false),
            Todo(title = "런닝 30분", isCompleted = true)
        ),
        listOf(
            Todo(title = "Kotlin 문법 정리", isCompleted = false),
            Todo(title = "Coroutine 복습", isCompleted = true),
            Todo(title = "Jetpack Compose", isCompleted = false)
        ),
        listOf(
            Todo(title = "10분 명상", isCompleted = true),
            Todo(title = "감사 일기 작성", isCompleted = false),
            Todo(title = "차분한 음악 듣기", isCompleted = true)
        )
    )


    LazyColumn {
        items(todoCategories.indices.toList()) { index ->
            val categoryTitle = todoCategories[index].name
            val categorySubTitle = todoCategories[index].description
            val taskList = todos[index]
            val total = taskList.size
            val progress = taskList.count { it.isCompleted }

            TodoCard(
                categoryTitle = categoryTitle,
                subtitle = categorySubTitle.toString(),
                progress = progress,
                total = total,
                todos = taskList.map { it.title to it.isCompleted }
            ) {
                navController.navigate("todo/detail/${todoCategories[index].categoryId}")
            }
        }
    }
}
