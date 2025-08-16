package com.mukmuk.todori.ui.screen.stats

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.mukmuk.todori.ui.screen.stats.component.StatsSegmentedTabs
import com.mukmuk.todori.ui.screen.stats.component.StatsTopBar
import com.mukmuk.todori.ui.screen.stats.tab.StatsTab
import com.mukmuk.todori.ui.screen.stats.tab.day.DayTab
import com.mukmuk.todori.ui.screen.stats.tab.month.MonthTab
import com.mukmuk.todori.ui.screen.stats.tab.week.WeekTab
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StatsScreen() {
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

    var selectedTab by remember { mutableStateOf(StatsTab.DAY) }

    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    var anchorDate by remember { mutableStateOf<LocalDate>(today) }

    Scaffold(
        topBar = { StatsTopBar(onMonthlyReportClick = { /* TODO */ }) }
    ) { inner ->
        Column(modifier = Modifier.fillMaxSize().padding(inner)) {
            StatsSegmentedTabs(
                selected = selectedTab,
                onSelect = { selectedTab = it },
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
            )

            when (selectedTab) {
                StatsTab.DAY -> DayTab(
                    uid = uid,
                    date = anchorDate,
                    onDateChange = { anchorDate = it }
                )
                StatsTab.WEEK -> WeekTab(
                    uid = uid,
                    date = anchorDate,
                    onDateChange = { anchorDate = it }
                )
                StatsTab.MONTH -> MonthTab(
                    uid = uid,
                    date = anchorDate,
                    onDateChange = { anchorDate = it }
                )
            }
        }
    }
}