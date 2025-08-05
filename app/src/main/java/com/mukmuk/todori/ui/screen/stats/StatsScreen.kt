package com.mukmuk.todori.ui.screen.stats

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.mukmuk.todori.data.remote.dailyRecords.DailyRecords
import com.mukmuk.todori.data.remote.goal.Goal
import com.mukmuk.todori.data.remote.todo.Todo
import com.mukmuk.todori.ui.screen.stats.tab.DayTab
import com.mukmuk.todori.ui.screen.stats.tab.MonthTab
import com.mukmuk.todori.ui.screen.stats.tab.WeekTab

//더미데이터
val records = listOf(
    DailyRecords("2025-07-24", "11111", 15432, null),
    DailyRecords("2025-07-25", "11111", 21541, null),
    DailyRecords("2025-07-26", "11111", 10513, null),
    DailyRecords("2025-07-27", "11111", 18414, null),
    DailyRecords("2025-07-28", "11111", 15931, null),
    DailyRecords("2025-07-29", "11111", 1479, "오늘은 뭔가 부족하다"),
    DailyRecords("2025-07-30", "11111", 12955, null),
    DailyRecords("2025-07-31", "11111", 20201, null),
    DailyRecords("2025-08-01", "11111", 12447, "투두 모두 완료ㅎㅎ"),
    DailyRecords("2025-08-02", "11111", 12345, "복습하자"),
    DailyRecords("2025-08-03", "11111", 23451, null),
    DailyRecords("2025-08-04", "11111", 12437, null),
    DailyRecords("2025-08-05", "11111", 8249, "오류지옥")
)
val todos = listOf(
    Todo(title = "유지보수", date = "2025-08-04", isCompleted = true),
    Todo(title = "걷기", date = "2025-08-04", isCompleted = true),
    Todo(title = "눈 감기", date = "2025-08-05", isCompleted = true),
    Todo(title = "잠 자기", date = "2025-08-05", isCompleted = false),
    Todo(title = "명상", date = "2025-08-05", isCompleted = true)
)

val goals = listOf(
    Goal(title = "책 펼치기", endDate = "2025-08-04", isCompleted = true),
    Goal(title = "일회독 하기", endDate = "2025-08-04", isCompleted = true),
    Goal(title = "노트 정리 하기", endDate = "2025-08-05", isCompleted = false),
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StatsScreen() {
    var selectedTabIndex by remember { mutableStateOf(0) }

    val tabs = listOf("DAY", "WEEK", "MONTH")

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = {selectedTabIndex = index},
                    text = { Text(title) }
                )
            }
        }

        when (selectedTabIndex) {
            0 -> DayTab(dayRecords = records, dayTodos = todos, dayGoals = goals)
            1 -> WeekTab(weekRecords = records, weekTodos = todos)
            2 -> MonthTab(monthRecords = records, monthTodos = todos)
        }
    }
}