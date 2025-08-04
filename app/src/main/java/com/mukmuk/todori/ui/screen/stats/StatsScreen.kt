package com.mukmuk.todori.ui.screen.stats

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
import com.mukmuk.todori.ui.screen.stats.tab.DayTab
import com.mukmuk.todori.ui.screen.stats.tab.MonthTab
import com.mukmuk.todori.ui.screen.stats.tab.WeekTab
import kotlinx.datetime.LocalDate

@Composable
fun StatsScreen() {
    var selectedTabIndex by remember { mutableStateOf(0) }

    var selectedDate by remember {
        mutableStateOf(LocalDate.parse("2025-08-04"))
    }


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
            0 -> DayTab(selectedDay = selectedDate)
            1 -> WeekTab(selectedWeek = selectedDate, onWeekChange = {selectedDate = it})
            2 -> MonthTab(selectedMonth = selectedDate, onMonthChange = {selectedDate = it})
        }
    }
}