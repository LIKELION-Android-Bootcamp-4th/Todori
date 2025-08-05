package com.mukmuk.todori.ui.screen.stats.tab

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mukmuk.todori.data.remote.dailyRecord.DailyRecord
import com.mukmuk.todori.ui.screen.stats.card.CalendarCard
import com.mukmuk.todori.ui.screen.stats.card.DayStatsCard
import com.mukmuk.todori.ui.theme.Dimens
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DayTab(dayRecords: List<DailyRecord>) {
    var selectedDay by remember { mutableStateOf(LocalDate.now()) }

    Log.d("aa", "선택날짜 : $selectedDay")

    val todayRecord = dayRecords.firstOrNull { LocalDate.parse(it.date) == selectedDay }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(Dimens.XLarge))
        CalendarCard(
            record = dayRecords,
            selectedDate = selectedDay,
            onDateSelected = { selectedDay = it })
        Spacer(modifier = Modifier.height(Dimens.Large))
//        todayRecord?.let {
//            DayStatsCard(record = it)
//        }
        DayStatsCard(record = dayRecords[11])
        Spacer(modifier = Modifier.height(Dimens.Large))
    }
}