package com.mukmuk.todori.ui.screen.todo

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.mukmuk.todori.ui.screen.todo.component.MenuAction
import com.mukmuk.todori.ui.screen.todo.component.TodoTopBar
import com.mukmuk.todori.ui.screen.todo.component.WeekCalendar
import com.mukmuk.todori.ui.screen.todo.list.goal.GoalTodoList
import com.mukmuk.todori.ui.screen.todo.list.study.StudyTodoList
import com.mukmuk.todori.ui.screen.todo.list.todo.TodoList
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.UserPrimary
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import java.time.YearMonth


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TodoScreen(navController: NavHostController) {
    val viewModel: TodoViewModel = hiltViewModel()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val studyRecordsMillis by viewModel.studyRecordsMillis.collectAsState()
    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }

    val tabs = listOf("개인", "목표", "스터디")

    val uid = Firebase.auth.currentUser?.uid.toString()
    LaunchedEffect(Unit) {
        val start = selectedDate.minus(selectedDate.dayOfWeek.isoDayNumber - 1, kotlinx.datetime.DateTimeUnit.DAY)
        val end = start.plus(6, kotlinx.datetime.DateTimeUnit.DAY)
        viewModel.onWeekVisible(uid, start, end)
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
                    // todo: DeepLink 처리
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
    }

}