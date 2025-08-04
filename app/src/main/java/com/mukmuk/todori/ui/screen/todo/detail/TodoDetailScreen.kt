package com.mukmuk.todori.ui.screen.todo.detail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mukmuk.todori.data.remote.todo.Todo
import com.mukmuk.todori.data.remote.todo.TodoCategory
import com.mukmuk.todori.ui.component.TodoItemEditableRow
import com.mukmuk.todori.ui.screen.todo.component.CardHeaderSection
import com.mukmuk.todori.ui.screen.todo.component.CommonDetailAppBar
import com.mukmuk.todori.ui.component.ProgressWithText
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Red
import com.mukmuk.todori.ui.theme.UserPrimary
import com.mukmuk.todori.ui.theme.White

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoDetailScreen(
    categoryId: String,
    navController: NavHostController,
    onBack: () -> Unit
) {
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

    val focusManager = LocalFocusManager.current
    val index = todoCategories.indexOfFirst { it.categoryId == categoryId }
    val category = todoCategories.getOrNull(index)
    val categoryTitle = category?.name.orEmpty()
    val categorySubTitle = category?.description.orEmpty()

    val taskList = remember { mutableStateListOf(*todos[index].toTypedArray()) }
    var newTodoText by remember { mutableStateOf("") }

    val total = taskList.size
    val progress = taskList.count { it.isCompleted }


    Column(modifier = Modifier.fillMaxSize()) {
        CommonDetailAppBar(
            title = categoryTitle,
            onBack = onBack,
            onEdit = {
                navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.set("editCategory", category)
                navController.navigate("category/create")
            },
            onDelete = {
                // todo:
            }
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(White)
                .padding(Dimens.Small)
        ) {
            CardHeaderSection(
                title = categoryTitle,
                subtitle = categorySubTitle,
                showArrowIcon = false
            )
            Spacer(modifier = Modifier.height(Dimens.Small))
            ProgressWithText(
                progress = progress / total.toFloat(),
                completed = progress,
                total = total,
            )
            Spacer(modifier = Modifier.height(Dimens.Small))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.Medium)
            ) {
                OutlinedTextField(
                    value = newTodoText,
                    onValueChange = { newTodoText = it },
                    placeholder = { Text("Todo 입력") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(Dimens.Small))
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(color = UserPrimary)
                ) {
                    IconButton(
                        onClick = {
                            if (newTodoText.isNotBlank()) {
                                taskList.add(
                                    Todo(title = newTodoText.trim(), isCompleted = false)
                                )
                                newTodoText = ""
                                focusManager.clearFocus()
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "할 일 추가",
                            tint = White
                        )
                    }
                }
            }
        }

        taskList.forEachIndexed { i, todo ->
            TodoItemEditableRow(
                title = todo.title,
                isDone = todo.isCompleted,
                modifier = Modifier.padding(Dimens.Small),
                onCheckedChange = { checked ->
                    taskList[i] = taskList[i].copy(isCompleted = checked)
                },
                trailingContent = {
                    Icon(
                        imageVector = Icons.Outlined.DeleteForever,
                        contentDescription = "삭제",
                        tint = Red,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable {
                                taskList.removeAt(i)
                            }
                    )
                }
            )
        }
    }
}
