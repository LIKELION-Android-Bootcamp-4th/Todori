package com.mukmuk.todori.ui.screen.stats.tab.day

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
fun DayTab(
    uid: String,
    date: kotlinx.datetime.LocalDate,
    onDateChange: (kotlinx.datetime.LocalDate) -> Unit
) {
    val viewModel: DayViewModel = hiltViewModel()

    val fetchedRecord by viewModel.selectedRecord.collectAsState()
    val monthRecords by viewModel.monthRecords.collectAsState()
    val selectedDay by viewModel.selectedDate.collectAsState()
    val todos by viewModel.todos.collectAsState()

    LaunchedEffect(date) {
        viewModel.onDateSelected(
            LocalDate.of(date.year, date.monthNumber, date.dayOfMonth)
        )
    }

    val recordFromList = remember(selectedDay, monthRecords) {
        monthRecords.firstOrNull {
            runCatching { LocalDate.parse(it.date.trim()) }.getOrNull() == selectedDay
        }
    }
    val recordForSelected = recordFromList ?: fetchedRecord

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(Dimens.XLarge))

        CalendarCard(
            record = monthRecords,
            selectedDate = selectedDay,
            onDateSelected = { picked ->
                viewModel.onDateSelected(picked)
                onDateChange(
                    kotlinx.datetime.LocalDate(picked.year, picked.monthValue, picked.dayOfMonth)
                )
            },
            onMonthChanged = { ym -> viewModel.onMonthChanged(ym) }
        )

        Spacer(modifier = Modifier.height(Dimens.Large))

        DayStatsCard(
            selectedDate = selectedDay,
            studyTimeMillis = recordForSelected?.studyTimeMillis ?: 0L,
            reflection = recordForSelected?.reflection,
            todos = todos,
            onReflectionChanged = { newReflection ->
                viewModel.updateDailyRecord(uid, selectedDay, newReflection)
            }
        )

        Spacer(modifier = Modifier.height(Dimens.Large))
    }
}