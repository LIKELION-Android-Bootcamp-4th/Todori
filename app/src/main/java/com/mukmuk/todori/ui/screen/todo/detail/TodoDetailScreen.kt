package com.mukmuk.todori.ui.screen.todo.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.data.remote.todo.Todo
import com.mukmuk.todori.data.remote.todo.TodoCategory
import com.mukmuk.todori.ui.component.TodoItemEditableRow
import com.mukmuk.todori.ui.screen.todo.component.TodoDetailHeader
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Red
import com.mukmuk.todori.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoDetailScreen(
    categoryId: String,
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
    var dropdownExpanded by remember { mutableStateOf(false) }
    val index = todoCategories.indexOfFirst { it.categoryId == categoryId }
    val category = todoCategories.getOrNull(index)
    val categoryTitle = category?.name.orEmpty()
    val categorySubTitle = category?.description.orEmpty()

    val taskList = remember { mutableStateListOf(*todos[index].toTypedArray()) }
    var newTodoText by remember { mutableStateOf("") }

    val total = taskList.size
    val progress = taskList.count { it.isCompleted }


    Column(modifier = Modifier.fillMaxSize()) {
        CenterAlignedTopAppBar(
            title = { Text("카테고리 상세") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(
                    onClick = {
                        dropdownExpanded = true
                    }
                ) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More")
                }
                DropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false },
                    modifier = Modifier.background(White)
                ) {
                    DropdownMenuItem(
                        text = { Text("수정", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
                        onClick = {
                            dropdownExpanded = false

                        }
                    )
                    DropdownMenuItem(
                        text = { Text("삭제", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
                        onClick = {
                            dropdownExpanded = false
                            // TODO: 삭제
                        }
                    )
                }
            }
        )
        TodoDetailHeader(
            categoryTitle, categorySubTitle, progress, total,
            newTodoText,
            onTodoTextChange = {newTodoText = it},
            onAddClick = {
                if (newTodoText.isNotBlank()) {
                    taskList.add(
                        Todo(title = newTodoText.trim(), isCompleted = false)
                    )
                    newTodoText = ""
                    focusManager.clearFocus()
                }
            }
        )

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
