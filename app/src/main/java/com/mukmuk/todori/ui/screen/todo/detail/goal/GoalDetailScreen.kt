package com.mukmuk.todori.ui.screen.todo.detail.goal

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.rounded.CheckCircleOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.mukmuk.todori.ui.component.ProgressWithText
import com.mukmuk.todori.ui.component.TodoItemEditableRow
import com.mukmuk.todori.ui.screen.todo.component.CardHeaderSection
import com.mukmuk.todori.ui.screen.todo.component.CommonDetailAppBar
import com.mukmuk.todori.ui.screen.todo.component.GoalMetaInfoRow
import com.mukmuk.todori.ui.screen.todo.component.SingleDatePickerBottomSheet
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.DarkGray
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Dimens.DefaultCornerRadius
import com.mukmuk.todori.ui.theme.GoalPrimary
import com.mukmuk.todori.ui.theme.Red
import com.mukmuk.todori.ui.theme.White
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toKotlinLocalDate

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GoalDetailScreen(
    goalId: String,
    navController: NavHostController,
    onBack: () -> Unit
) {
    val uid = Firebase.auth.currentUser?.uid.toString()
    val viewModel: GoalDetailViewModel = hiltViewModel()

    val state by viewModel.state.collectAsState()
    var newTodoTitle by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }
    var newTodoDueDate by remember { mutableStateOf<LocalDate?>(null) }

    var showDeleteDialog by remember { mutableStateOf(false) }


    var context = LocalContext.current
    LaunchedEffect(goalId) {
        viewModel.loadGoalDetail(uid, goalId)
    }

    val goal = state.goal
    val todos = state.todos

    val total = todos.size
    val progress = todos.count { it.completed }

    if (state.goalDeleted) {
        LaunchedEffect(Unit) {
            onBack()
            viewModel.resetGoalDeleted()
            Toast.makeText(context, "목표가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    if (goal != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            CommonDetailAppBar(
                title = goal.title,
                onBack = onBack,
                onEdit = {
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("goal", goal)
                    navController.navigate("goal/create")
                },
                onDelete = {
                    showDeleteDialog = true
                }
            )

            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("목표 삭제") },
                    text = { Text("이 목표와 관련된 모든 세부 목표가 함께 삭제됩니다. 진행할까요?") },
                    confirmButton = {
                        Text(
                            "삭제",
                            color = Red,
                            modifier = Modifier.clickable {
                                showDeleteDialog = false
                                viewModel.deleteGoalWithTodos(uid, goal.goalId)
                            }
                        )
                    },
                    dismissButton = {
                        Text(
                            "취소",
                            modifier = Modifier.clickable {
                                showDeleteDialog = false
                            }
                        )
                    }
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(White)
                    .padding(Dimens.Small)
            ) {
                CardHeaderSection(
                    title = goal.title,
                    subtitle = goal.description,
                    showArrowIcon = false
                )

                Spacer(modifier = Modifier.height(Dimens.Small))

                GoalMetaInfoRow(goal)

                Spacer(modifier = Modifier.height(Dimens.Small))

                ProgressWithText(
                    progress = if (total == 0) 0f else progress / total.toFloat(),
                    completed = progress,
                    total = total,
                    progressColor = GoalPrimary,
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = Dimens.Nano
                )

                Spacer(modifier = Modifier.height(Dimens.Medium))

                Text("세부 목표", style = AppTextStyle.Body.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(Dimens.Small))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = newTodoTitle,
                        onValueChange = { newTodoTitle = it },
                        modifier = Modifier
                            .weight(1f)
                            .padding(Dimens.Nano),
                        placeholder = { Text("세부 목표 입력") }
                    )

                    Spacer(modifier = Modifier.width(Dimens.Nano))

                    IconButton(
                        onClick = { showDatePicker = true },
                        modifier = Modifier
                            .size(56.dp)
                            .border(1.dp, DarkGray, RoundedCornerShape(DefaultCornerRadius))
                    ) {
                        BadgedBox(
                            badge = {
                                if (newTodoDueDate != null) {
                                    Badge()
                                }
                            }
                        ) {
                            Icon(Icons.Outlined.CalendarMonth, contentDescription = "마감일 선택")
                        }
                    }

                    SingleDatePickerBottomSheet(
                        show = showDatePicker,
                        onDismissRequest = { showDatePicker = false },
                        onConfirm = { selectedDate ->
                            newTodoDueDate = selectedDate.toKotlinLocalDate()
                        }
                    )

                    Spacer(modifier = Modifier.width(Dimens.Nano))

                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                color = GoalPrimary,
                                shape = RoundedCornerShape(DefaultCornerRadius)
                            )
                    ) {
                        IconButton(
                            onClick = {
                                if (newTodoTitle.isNotBlank() && newTodoDueDate != null) {
                                    viewModel.addGoalTodo(
                                        uid = uid,
                                        goalId = goal.goalId,
                                        title = newTodoTitle.trim(),
                                        dueDate = newTodoDueDate?.toString(),
                                        onResult = { success ->
                                            if (success) {
                                                newTodoTitle = ""
                                                newTodoDueDate = null
                                            }
                                        }
                                    )
                                } else if (newTodoDueDate == null) {
                                    Toast.makeText(context, "마감일을 입력해주세요.", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "추가")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(Dimens.Small))

            if (todos.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize().weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircleOutline,
                        contentDescription = "Todo Empty",
                        modifier = Modifier.size(38.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(Dimens.Tiny))
                    Text(
                        "할 일이 없습니다",
                        style = AppTextStyle.BodyLarge,
                        color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.height(Dimens.Tiny))
                    Text(
                        "목표를 위한 세부 할 일들을 추가해 보세요!",
                        style = AppTextStyle.BodySmall,
                        color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.height(Dimens.Medium))
                }
            } else {
                val sortedTodos = todos.sortedBy { it.completed }
                sortedTodos.forEachIndexed { index, todo ->
                    TodoItemEditableRow(
                        title = todo.title,
                        isDone = todo.completed,
                        dueDate = todo.dueDate,
                        modifier = Modifier.padding(Dimens.Small),
                        onCheckedChange = { checked ->
                            viewModel.toggleGoalTodoCompleted(uid, todo)
                        },
                        trailingContent = {
                            Icon(
                                imageVector = Icons.Outlined.DeleteForever,
                                contentDescription = "삭제",
                                tint = Red,
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable {
                                        viewModel.deleteGoalTodo(uid, todo.goalTodoId, todo.goalId)
                                    }
                            )
                        }
                    )
                }
            }
        }
    }
}