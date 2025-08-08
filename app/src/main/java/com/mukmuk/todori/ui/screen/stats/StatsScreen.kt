package com.mukmuk.todori.ui.screen.stats

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.data.remote.dailyRecord.DailyRecord
import com.mukmuk.todori.ui.screen.stats.tab.day.DayTab
import com.mukmuk.todori.ui.screen.stats.tab.month.MonthTab
import com.mukmuk.todori.ui.screen.stats.tab.week.WeekTab
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.UserPrimary

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
            0 -> DayTab(dayRecords = records)
            1 -> WeekTab(weekRecords = records)
            2 -> MonthTab(monthRecords = records)
        }
    }
}