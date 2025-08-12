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
import com.google.firebase.auth.FirebaseAuth
import com.mukmuk.todori.data.remote.dailyRecord.DailyRecord
import com.mukmuk.todori.ui.screen.stats.tab.day.DayTab
import com.mukmuk.todori.ui.screen.stats.tab.month.MonthTab
import com.mukmuk.todori.ui.screen.stats.tab.week.WeekTab
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.UserPrimary
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StatsScreen() {
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("DAY", "WEEK", "MONTH")

    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    var anchorDate by remember { mutableStateOf<LocalDate>(today) }

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
            0 -> DayTab(
                uid = uid,
                date = anchorDate,
                onDateChange = { anchorDate = it }
            )
            1 -> WeekTab(
                uid = uid,
                date = anchorDate,
                onDateChange = { anchorDate = it }
            )
            2 -> MonthTab(
                uid = uid,
                date = anchorDate,
                onDateChange = { anchorDate = it }
            )
        }
    }
}