package com.mukmuk.todori.ui.screen.todo.detail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mukmuk.todori.data.remote.goal.Goal
import com.mukmuk.todori.data.remote.goal.GoalTodo
import com.mukmuk.todori.ui.component.TodoItemEditableRow
import com.mukmuk.todori.ui.screen.todo.component.CardHeaderSection
import com.mukmuk.todori.ui.screen.todo.component.GoalPeriodStatus
import com.mukmuk.todori.ui.screen.todo.component.GoalProgressSection
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
    goal: Goal,
    navController: NavHostController,
    onBack: ()-> Unit
) {
    var dropdownExpanded by remember { mutableStateOf(false) }
    var newTodoTitle by remember { mutableStateOf("") }
    val goalTodos = remember {
        mutableStateListOf(
            GoalTodo(
                title = "자료 조사",
                dueDate = "2025-08-10",
                isCompleted = false
            ),
            GoalTodo(
                title = "초안 작성",
                dueDate = "2025-08-15",
                isCompleted = true
            ),
            GoalTodo(
                title = "최종 검토",
                dueDate = "2025-08-20",
                isCompleted = false
            )
        )
    }


    var showDatePicker by remember { mutableStateOf(false) }
    var newTodoDueDate by remember { mutableStateOf<LocalDate?>(null) }



    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("목표 상세") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { dropdownExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("수정") },
                            onClick = {
                                dropdownExpanded = false

                                navController.currentBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("goal", goal)
                                navController.navigate("goal/create")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("삭제") },
                            onClick = {
                                dropdownExpanded = false
                                // TODO : 삭제
                            }
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier.background(color = White).padding(Dimens.Medium)
            ) {
                // 헤더
                CardHeaderSection(
                    title = goal.title,
                    subtitle = goal.description,
                    showArrowIcon = false
                )

                Spacer(modifier = Modifier.height(Dimens.Small))

                // 기간 + 디데이 + 진행상태
                GoalPeriodStatus(goal)

                Spacer(modifier = Modifier.height(Dimens.Small))

                // 진행률
                GoalProgressSection(goal)

                Spacer(modifier = Modifier.height(Dimens.Medium))

                // 세부 목표 리스트 추가 영역 (생성용 + 리스트)
                Text("세부 목표", style = AppTextStyle.Body.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(Dimens.Small))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = newTodoTitle,
                        onValueChange = { newTodoTitle = it },
                        modifier = Modifier.weight(1f).padding(Dimens.Nano),
                        placeholder = { Text("세부 목표 입력") }
                    )

                    Spacer(modifier = Modifier.width(Dimens.Nano))

                    IconButton(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.size(56.dp).border(1.dp, DarkGray, RoundedCornerShape(DefaultCornerRadius))
                    ) {
                        Icon(Icons.Outlined.CalendarMonth, contentDescription = "마감일 선택")
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
                            .background(color = GoalPrimary)
                    ) {
                        IconButton(
                            onClick = {
                                if (newTodoTitle.isNotBlank()) {
                                    goalTodos.add(
                                        GoalTodo(
                                            title = newTodoTitle,
                                            dueDate = newTodoDueDate?.toString() ?: "",
                                            isCompleted = false
                                        )
                                    )
                                    newTodoTitle = ""
                                    newTodoDueDate = null
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

            // 리스트
            goalTodos.forEachIndexed { index, todo ->
                TodoItemEditableRow(
                    title = todo.title,
                    isDone = todo.isCompleted,
                    dueDate = todo.dueDate,
                    modifier = Modifier.padding(Dimens.Small),
                    onCheckedChange = { checked ->
                        goalTodos[index] = goalTodos[index].copy(isCompleted = checked)
                    },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.Outlined.DeleteForever,
                            contentDescription = "삭제",
                            tint = Red,
                            modifier = Modifier
                                .size(20.dp)
                                .clickable {
                                    goalTodos.removeAt(index)
                                }
                        )
                    }
                )
            }
        }
    }
}