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
import com.mukmuk.todori.ui.screen.stats.tab.DayTab
import com.mukmuk.todori.ui.screen.stats.tab.MonthTab
import com.mukmuk.todori.ui.screen.stats.tab.WeekTab
import kotlinx.datetime.LocalDate

data class DailyRecord(
    val selectedDay: LocalDate, //선택 날짜
    val studySeconds: Int, //공부 시간
    val completedTodos: Int, //완료 투두
    val totalTodos: Int, //총 투두
    var reflection: String? //한 줄 회고
)

//더미데이터
val records = listOf(
    DailyRecord(LocalDate.parse("2025-07-24"), 15432, 2, 4,null),
    DailyRecord(LocalDate.parse("2025-07-25"), 21541, 3, 5, null),
    DailyRecord(LocalDate.parse("2025-07-26"), 10513, 4, 4, null),
    DailyRecord(LocalDate.parse("2025-07-27"), 18414, 2, 4,null),
    DailyRecord(LocalDate.parse("2025-07-28"), 15931, 3, 5, null),
    DailyRecord(LocalDate.parse("2025-07-29"), 1479, 4, 4, "오늘은 뭔가 부족하다"),
    DailyRecord(LocalDate.parse("2025-07-30"), 12955, 2, 4,null),
    DailyRecord(LocalDate.parse("2025-07-31"), 20201, 3, 5, null),
    DailyRecord(LocalDate.parse("2025-08-01"), 12447, 4, 4, "투두 모두 완료ㅎㅎ"),
    DailyRecord(LocalDate.parse("2025-08-02"), 12345, 2, 4,"복습하자"),
    DailyRecord(LocalDate.parse("2025-08-03"), 23451, 3, 5, null),
    DailyRecord(LocalDate.parse("2025-08-04"), 12437, 4, 4, "투두 모두 완료!"),
    DailyRecord(LocalDate.parse("2025-08-05"), 8249, 2, 3, "오류지옥")

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