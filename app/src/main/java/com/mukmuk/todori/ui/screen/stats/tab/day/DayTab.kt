package com.mukmuk.todori.ui.screen.stats.tab.day

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.mukmuk.todori.data.remote.dailyRecord.DailyRecord
import com.mukmuk.todori.ui.screen.stats.component.CalendarCard
import com.mukmuk.todori.ui.screen.stats.component.DayStatsCard
import com.mukmuk.todori.ui.theme.Dimens
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DayTab(dayRecords: List<DailyRecord>) {
    var selectedDay by remember { mutableStateOf(LocalDate.now()) }
    val viewModel: DayViewModel = hiltViewModel()
    val uid = "testuser"

    val todayRecord = dayRecords.firstOrNull { LocalDate.parse(it.date) == selectedDay }
    val dailyTodos by viewModel.todos.collectAsState()
    val dailyCompletedTodos by viewModel.completedTodos.collectAsState()
    val dailyRecord by viewModel.dailyRecord.collectAsState()

    Log.d("aa","$selectedDay")
    Log.d("aa","$dailyRecord")


    LaunchedEffect(uid, selectedDay) {
        viewModel.loadTodos(uid = uid, date = selectedDay)
        viewModel.loadDailyRecord(uid = uid, date = selectedDay)
    }

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
        DayStatsCard(date = selectedDay, record = dailyRecord, todos = dailyTodos, completedTodos = dailyCompletedTodos)
        Spacer(modifier = Modifier.height(Dimens.Large))
    }
}