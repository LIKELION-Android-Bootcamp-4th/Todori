package com.mukmuk.todori.ui.screen.todo.list.todo

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
import com.mukmuk.todori.ui.screen.todo.component.TodoCard
import com.mukmuk.todori.util.toJavaLocalDate
import kotlinx.datetime.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TodoList(selectedDate: LocalDate, navController: NavHostController) {
    val viewModel: TodoListViewModel = hiltViewModel()


    val state by viewModel.state.collectAsState()
    val uid = "testuser"

    LaunchedEffect(selectedDate) {
        viewModel.loadTodoList(uid, selectedDate.toJavaLocalDate())
    }


    LazyColumn {
        items(state.categories) { category ->
            val todos = state.todosByCategory[category.categoryId].orEmpty()
            val total = todos.size
            val progress = todos.count { it.completed }
            TodoCard(
                categoryTitle = category.name,
                subtitle = category.description.orEmpty(),
                progress = progress,
                total = total,
                todos = todos.map { it.title to it.completed }
            ) {
                navController.navigate("todo/detail/${category.categoryId}?date=${selectedDate}")
            }
        }
    }
}
