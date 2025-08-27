package com.mukmuk.todori.ui.screen.todo.list.todo

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.mukmuk.todori.ui.screen.todo.component.TodoCard
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.DarkGray
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Gray
import com.mukmuk.todori.ui.theme.White
import com.mukmuk.todori.util.toJavaLocalDate
import kotlinx.datetime.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TodoList(selectedDate: LocalDate, navController: NavHostController) {
    val viewModel: TodoListViewModel = hiltViewModel()


    val state by viewModel.state.collectAsState()
//    val uid = "testuser"

    val uid = Firebase.auth.currentUser?.uid.toString()

    val option = listOf("나의 카테고리", "공유된 카테고리")

    LaunchedEffect(selectedDate, state.selectedOption) {
        if (state.selectedOption == "나의 카테고리") {
            viewModel.loadTodoList(uid, selectedDate.toJavaLocalDate())
        } else {
            viewModel.loadSendTodoList(uid, selectedDate.toJavaLocalDate())
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            items(option) { item ->
                Box(
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = Gray,
                            shape = RoundedCornerShape(30)
                        )
                        .background(
                            if (state.selectedOption == item) Black else White,
                            shape = RoundedCornerShape(30)
                        )
                        .clickable {
                            viewModel.setSelectedOption(item)
                        }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = item,
                        fontSize = 14.sp,
                        style = AppTextStyle.Body,
                        color = if (state.selectedOption == item) White else Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(Dimens.Tiny))

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
            if(state.sendCategories.isNotEmpty()){
                items(state.sendCategories) { category ->
                    val todos = state.sendTodosByCategory[category.categoryId].orEmpty()
                    val total = todos.size
                    val progress = todos.count { it.completed }
                    TodoCard(
                        categoryTitle = category.name,
                        category = category,
                        userName = state.userMap[category.categoryId],
                        subtitle = category.description.orEmpty(),
                        progress = progress,
                        total = total,
                        todos = todos.map { it.title to it.completed }
                    ) {
                        navController.navigate("sendTodo/detail/${category.categoryId}?date=${selectedDate}")
                    }
                }
            }
        }

    }

}
