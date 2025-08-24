package com.mukmuk.todori.ui.screen.stats.tab.week

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mukmuk.todori.ui.screen.stats.component.week.StudyStatisticsSection
import com.mukmuk.todori.ui.screen.stats.component.week.TimePerformanceHeatmap
import com.mukmuk.todori.ui.screen.stats.component.week.WeekInsights
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.White
import com.mukmuk.todori.util.buildBarEntries
import com.mukmuk.todori.util.buildHeatmapData
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeekTab(
    uid: String,
    date: LocalDate,
    onDateChange: (LocalDate) -> Unit
) {
    val viewModel: WeekViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(uid, date) {
        val jDate = java.time.LocalDate.of(date.year, date.monthNumber, date.dayOfMonth)
        viewModel.loadWeekTodos(uid = uid, date = jDate)
        viewModel.loadWeekStudy(uid = uid, date = jDate)
        viewModel.loadStudyTargets(uid)
    }

    val weekStart = DayOfWeek.SUNDAY

    val weekNo = remember(date) {
        val firstOfMonth = LocalDate(date.year, date.monthNumber, 1)
        val offset = (firstOfMonth.dayOfWeek.ordinal - weekStart.ordinal + 7) % 7
        (offset + date.dayOfMonth - 1) / 7 + 1
    }

    val weekRange = remember(date) {
        val jDate = java.time.LocalDate.of(date.year, date.monthNumber, date.dayOfMonth)
        viewModel.getWeekRange(jDate)
    }

    val (totalEntries, completedEntries) = remember(
        state.todos,
        state.completedTodoItems,
        weekRange
    ) {
        buildBarEntries(state.todos, state.completedTodoItems, weekRange)
    }

    val dailyTargetMinutes = state.studyTargets?.dailyMinutes ?: 0


    val plannedHours = FloatArray(7) { dailyTargetMinutes / 60f }

    val actualHours = weekRange.map { date ->
        val record = state.dailyRecords.find { it.date == date.toString() }

        (record?.studyTimeMillis ?: 0L) / 1000f / 60f / 60f
    }.toFloatArray()

    val heatmapData = remember(state.dailyRecords, weekRange) {
        buildHeatmapData(weekRange, state.dailyRecords)
    }


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.Medium)
        ) {
            Row(
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                IconButton(onClick = {
                    onDateChange(date.minus(DatePeriod(days = 7)))
                }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Black
                    )
                }
            }
            Row(
                modifier = Modifier.align(Alignment.Center)
            ) {
                Text(
                    "${date.monthNumber}월 ${weekNo}주차",
                    style = AppTextStyle.TitleSmall
                )
            }
            Row(
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                IconButton(onClick = {
                    onDateChange(date.plus(DatePeriod(days = 7)))
                }) {
                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Black
                    )
                }
            }
        }

        StudyStatisticsSection(
            record = state.dailyRecords,
            allTodos = state.todos,
            completedTodos = state.completedTodoItems,
            totalEntries = totalEntries,
            completedEntries = completedEntries,
            plannedHours = plannedHours,
            actualHours = actualHours
        )

        Spacer(modifier = Modifier.height(Dimens.Large))


        TimePerformanceHeatmap(heatmapData)
        Spacer(modifier = Modifier.height(Dimens.Large))

        WeekInsights(state.insights)
        Spacer(modifier = Modifier.height(Dimens.Large))
    }
}