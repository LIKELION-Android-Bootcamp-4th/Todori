package com.mukmuk.todori.ui.screen.todo.list.todo

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.mukmuk.todori.ui.screen.todo.component.TodoCard
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.Gray
import com.mukmuk.todori.ui.theme.White
import com.mukmuk.todori.util.toJavaLocalDate
import kotlinx.datetime.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TodoList(selectedDate: LocalDate, navController: NavHostController) {
    val viewModel: TodoListViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()
    val uid = Firebase.auth.currentUser?.uid.toString()
    val option = listOf("나의 카테고리", "공유된 카테고리")

    LaunchedEffect(selectedDate, state.selectedOption) {
        if (state.selectedOption == "나의 카테고리") {
            viewModel.loadTodoList(uid, selectedDate.toJavaLocalDate())
        } else {
            viewModel.loadSendTodoList(uid, selectedDate.toJavaLocalDate())
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        Spacer(modifier = Modifier.height(Dimens.Tiny))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            items(option) { item ->
                Button(
                    onClick = { viewModel.setSelectedOption(item) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (state.selectedOption == item) Black else White,
                        contentColor = if (state.selectedOption == item) White else Black,
                    ),
                    shape = RoundedCornerShape(30),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                    border = ButtonDefaults.outlinedButtonBorder
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

        Spacer(modifier = Modifier.height(12.dp))

        if (state.selectedOption == "나의 카테고리") {
            if (state.categories.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircleOutline,
                        contentDescription = "Category Empty",
                        modifier = Modifier.size(38.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(Dimens.Tiny))
                    Text("생성된 카테고리가 없습니다", style = AppTextStyle.BodyLarge, color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(Dimens.Tiny))
                    Text("새로운 카테고리를 만들고 할 일을 추가해 보세요!", style = AppTextStyle.BodySmall, color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(Dimens.Medium))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
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
                            navController.navigate("todo/detail/${category.categoryId}?date=$selectedDate")
                        }
                    }
                }
            }
        } else {
            if (state.sendCategories.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircleOutline,
                        contentDescription = "Category Empty",
                        modifier = Modifier.size(38.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(Dimens.Tiny))
                    Text("공유된 카테고리가 없습니다", style = AppTextStyle.BodyLarge, color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(Dimens.Tiny))
                    Text("다른 사람의 카테고리 활동을 볼 수 있어요!", style = AppTextStyle.BodySmall, color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(Dimens.Medium))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
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
                            navController.navigate("sendTodo/detail/${category.categoryId}?date=$selectedDate")
                        }
                    }
                }
            }
        }
    }
}