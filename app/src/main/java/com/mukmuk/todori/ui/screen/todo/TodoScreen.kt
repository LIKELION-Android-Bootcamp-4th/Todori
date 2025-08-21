package com.mukmuk.todori.ui.screen.todo

import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.mukmuk.todori.data.remote.todo.TodoCategory
import com.mukmuk.todori.ui.screen.todo.component.MenuAction
import com.mukmuk.todori.ui.screen.todo.component.TodoCategoryPickerBottomSheet
import com.mukmuk.todori.ui.screen.todo.component.TodoTopBar
import com.mukmuk.todori.ui.screen.todo.component.WeekCalendar
import com.mukmuk.todori.ui.screen.todo.list.goal.GoalTodoList
import com.mukmuk.todori.ui.screen.todo.list.study.StudyTodoList
import com.mukmuk.todori.ui.screen.todo.list.todo.TodoList
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.ButtonPrimary
import com.mukmuk.todori.ui.theme.UserPrimary
import com.mukmuk.todori.ui.theme.White
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import java.time.YearMonth


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TodoScreen(navController: NavHostController, categoryId: String? = null) {
    val viewModel: TodoViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val selectedDate by viewModel.selectedDate.collectAsState()
    val studyRecordsMillis by viewModel.studyRecordsMillis.collectAsState()
    var selectedTabIndex by remember { mutableStateOf(0) }
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf<TodoCategory?>(null) }

    val tabs = listOf("개인", "목표", "스터디")

//    val uid= "testuser"
    val uid = Firebase.auth.currentUser?.uid.toString()
    LaunchedEffect(Unit) {
        val start = selectedDate.minus(selectedDate.dayOfWeek.isoDayNumber - 1, kotlinx.datetime.DateTimeUnit.DAY)
        val end = start.plus(6, kotlinx.datetime.DateTimeUnit.DAY)
        viewModel.onWeekVisible(uid, start, end)
    }

    LaunchedEffect(state.sendUrl != null){
        Log.d("TodoScreen", "state.sendUrl: ${state.sendUrl}")
        if(state.sendUrl != null){
            val url = state.sendUrl ?: ""
            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, url)
            }
            context.startActivity(Intent.createChooser(sendIntent, "send"))
            viewModel.clearSendTodoCategory()
        }
    }

    LaunchedEffect(categoryId) {
        if (categoryId != null && !state.setCategoryState) {
            showDialog = true
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TodoTopBar(selectedYearMonth = YearMonth.of(selectedDate.year, selectedDate.month)) { action ->
            when (action) {
                MenuAction.CreatePersonalCategory -> {
                    navController.navigate("category/create")
                }
                MenuAction.CreateGoalRoadmap -> {
                    navController.navigate("goal/create")
                }
                MenuAction.CreateStudy -> {
                    navController.navigate("study/create")
                }
                else -> {
                    viewModel.getTodoCategories(uid)
                    showBottomSheet = true
                }
            }
        }


        //주 캘린더
        WeekCalendar(
            selectedDate = selectedDate,
            onDateSelected = { viewModel.setSelectedDate(it) },
            studyRecordsMillis = studyRecordsMillis,
            onWeekVisible = { start, end -> viewModel.onWeekVisible(uid, start, end) },
            anchorDate = selectedDate
        )

        //탭 생성
        TabRow(
            selectedTabIndex = selectedTabIndex,
            indicator = { tabPositions ->
                Box(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[selectedTabIndex])
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(UserPrimary)
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title, style = AppTextStyle.Body.copy(color = Black)) }
                )
            }
        }

        when (selectedTabIndex) {
            0 -> TodoList(selectedDate,navController)
            1 -> GoalTodoList(selectedDate,navController)
            2 -> StudyTodoList(selectedDate,navController)
        }

        if (showDialog) {

            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = {
                    if (categoryId != null && !state.setCategoryState) {
                        Text("카테고리 추가", style = AppTextStyle.Title)
                    } else {
                        Text("카테고리 공유", style = AppTextStyle.Title)
                    }
                },
                text = {
                    if (categoryId != null && !state.setCategoryState) {
                        Text("공유 받은 카테고리를 추가하시겠습니까?", style = AppTextStyle.Body)
                    } else {
                        Text("선택한 ${selectedCategory?.name} 카테고리를 공유하시겠습니까?", style = AppTextStyle.Body)
                    }

                },
                containerColor = White,
                confirmButton = {
                    TextButton(onClick = {
                        if (categoryId != null && !state.setCategoryState) {
                            viewModel.addTodoCategoryFromUrl(uid, categoryId)
                            viewModel.setCategoryState(true)
                        } else {
                            selectedCategory?.let {
                                viewModel.sendTodoCategory(it)
                            }
                        }
                        showDialog = false
                    }) {
                        Text(
                            "확인",
                            color = ButtonPrimary,
                            style = AppTextStyle.Body.copy(color = White)
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        if (categoryId != null && !state.setCategoryState) {
                            viewModel.setCategoryState(true)
                        }
                        showDialog = false
                    }) {
                        Text("취소", color = Black, style = AppTextStyle.Body.copy(color = White))
                    }
                }
            )

        }

        if(showBottomSheet){

            TodoCategoryPickerBottomSheet(
                state.categories,
                state.todosByCategory,
                show = showBottomSheet,
                onDismissRequest = { showBottomSheet = false },
                onSelected = {
                    selectedCategory = it
                    showDialog = true
                    showBottomSheet = false
                }
            )

        }
    }

}