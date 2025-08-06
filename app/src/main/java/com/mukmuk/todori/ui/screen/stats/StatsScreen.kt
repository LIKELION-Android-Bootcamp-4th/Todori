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
import com.mukmuk.todori.data.remote.dailyRecord.DailyRecord
import com.mukmuk.todori.ui.screen.stats.tab.DayTab
import com.mukmuk.todori.ui.screen.stats.tab.MonthTab
import com.mukmuk.todori.ui.screen.stats.tab.WeekTab

//더미데이터
val records = listOf(
    DailyRecord("2025-07-24", "11111", 35432, null),
    DailyRecord("2025-07-25", "11111", 21541, null),
    DailyRecord("2025-07-26", "11111", 1513, null),
    DailyRecord("2025-07-27", "11111", 38414, null),
    DailyRecord("2025-07-28", "11111", 7100, null),
    DailyRecord("2025-07-29", "11111", 1479, "오늘은 뭔가 부족하다"),
    DailyRecord("2025-07-30", "11111", 12955, null),
    DailyRecord("2025-07-31", "11111", 20201, null),
    DailyRecord("2025-08-01", "11111", 12447, "투두 모두 완료ㅎㅎ"),
    DailyRecord("2025-08-02", "11111", 0, "복습하자"),
    DailyRecord("2025-08-03", "11111", 35431, null),
    DailyRecord("2025-08-04", "11111", 12437, null),
    DailyRecord("2025-08-05", "11111", 2249, "오류지옥")
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
            0 -> DayTab(dayRecords = records)
            1 -> WeekTab(weekRecords = records)
            2 -> MonthTab(monthRecords = records)
        }
    }
}