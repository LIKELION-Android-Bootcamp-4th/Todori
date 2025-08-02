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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mukmuk.todori.ui.screen.todo.component.GoalTodoList
import com.mukmuk.todori.ui.screen.todo.component.MenuAction
import com.mukmuk.todori.ui.screen.todo.component.StudyTodoList
import com.mukmuk.todori.ui.screen.todo.component.TodoList
import com.mukmuk.todori.ui.screen.todo.component.TodoTopBar
import com.mukmuk.todori.ui.screen.todo.component.WeekCalendar
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.UserPrimary
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import java.time.YearMonth


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TodoScreen(navController: NavHostController) {
    var selectedDate by remember {
        mutableStateOf(Clock.System.todayIn(TimeZone.currentSystemDefault()))
    }
    var selectedTabIndex by remember { mutableStateOf(0) }

    val tabs = listOf("개인", "목표", "스터디")

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TodoTopBar(selectedYearMonth = YearMonth.of(selectedDate.year, selectedDate.month)) { action ->
            when (action) {
                MenuAction.CreatePersonalCategory -> {
                    navController.navigate("category/create")
                }
                else -> {
                    //todo
                }
            }
        }


        WeekCalendar(
            selectedDate = selectedDate,
            onDateSelected = { selectedDate = it },
            studyRecords = mapOf(
                LocalDate.parse("2025-07-29") to 260,
                LocalDate.parse("2025-07-30") to 150,
                LocalDate.parse("2025-07-31") to 30,
            )
        )



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
            0 -> TodoList(selectedDate)
            1 -> GoalTodoList()
            2 -> StudyTodoList()
        }
    }

}