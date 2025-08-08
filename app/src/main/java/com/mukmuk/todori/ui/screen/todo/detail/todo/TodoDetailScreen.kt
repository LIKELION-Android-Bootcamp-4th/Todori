package com.mukmuk.todori.ui.screen.todo.detail.todo

import android.os.Build
import android.widget.Toast
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.mukmuk.todori.ui.component.ProgressWithText
import com.mukmuk.todori.ui.component.TodoItemEditableRow
import com.mukmuk.todori.ui.screen.home.HomeViewModel
import com.mukmuk.todori.ui.screen.todo.component.CardHeaderSection
import com.mukmuk.todori.ui.screen.todo.component.CommonDetailAppBar
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Red
import com.mukmuk.todori.ui.theme.UserPrimary
import com.mukmuk.todori.ui.theme.White

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoDetailScreen(
    categoryId: String,
    date: String,
    navController: NavHostController,
    onBack: () -> Unit
) {
    val viewModel: TodoDetailViewModel = hiltViewModel()
    val homeViewModel: HomeViewModel = hiltViewModel()
    val uid = "testuser"
    val state by viewModel.state.collectAsState()


    val focusManager = LocalFocusManager.current

    var showDialog by remember { mutableStateOf(false) }
    val category = state.category
    val categoryTitle = state.category?.name.orEmpty()
    val categorySubTitle = state.category?.description.orEmpty()
    val todos = state.todos
    val total = todos.size
    val progress = todos.count { it.completed }

    var newTodoText by remember { mutableStateOf("") }
    var context = LocalContext.current
    if (state.categoryDeleted) {
        LaunchedEffect(Unit) {
            onBack()
            viewModel.resetCategoryDeleted()
            homeViewModel.triggerRefresh()
        }
    }

    LaunchedEffect(categoryId, date) {
        viewModel.loadDetail(uid, categoryId, date)
    }

    if (showDialog) {
        AlertDialog(
            containerColor = White,
            onDismissRequest = { showDialog = false },
            title = { Text("카테고리 삭제") },
            text = { Text("이 카테고리와 연관된 모든 할 일이 함께 삭제됩니다. 진행할까요?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        viewModel.deleteCategoryWithTodos(uid, categoryId)
                        Toast.makeText(context,"카테고리가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                    }
                ) { Text("삭제", style = AppTextStyle.Body.copy(color = Red)) }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("취소",style = AppTextStyle.Body.copy(color = Black)) }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        CommonDetailAppBar(
            title = categoryTitle,
            onBack = onBack,
            onEdit = {
                navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.set("editCategory", category)
                navController.navigate("category/create")
                homeViewModel.triggerRefresh()
            },
            onDelete = {
                showDialog = true
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
                                viewModel.addTodo(
                                    uid = uid,
                                    categoryId = categoryId,
                                    date = date,
                                    title = newTodoText.trim(),
                                    onResult = { success ->
                                        if (success) {
                                            newTodoText = ""
                                            focusManager.clearFocus()
                                        }
                                    }
                                )
                                homeViewModel.triggerRefresh()
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

        todos.forEachIndexed { i, todo ->
            TodoItemEditableRow(
                title = todo.title,
                isDone = todo.completed,
                modifier = Modifier.padding(Dimens.Small),
                onCheckedChange = { checked ->
                    viewModel.toggleTodoCompleted(uid, todo)
                },
                trailingContent = {
                    Icon(
                        imageVector = Icons.Outlined.DeleteForever,
                        contentDescription = "삭제",
                        tint = Red,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable {
                                viewModel.deletedTodo(uid,todo.todoId,categoryId,date)
                            }
                    )
                }
            )
        }
    }
}
