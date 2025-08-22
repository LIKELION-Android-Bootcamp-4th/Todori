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
    }

    val weekStart = DayOfWeek.SUNDAY

    val weekNo = remember(date) {
        val firstOfMonth = LocalDate(date.year, date.monthNumber, 1)
        val offset = (firstOfMonth.dayOfWeek.ordinal - weekStart.ordinal + 7) % 7
        (offset + date.dayOfMonth - 1) / 7 + 1
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(White)
    ) {
        // ===== 상단 주차 이동 & 표시 =====
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

        // ===== 통계 + 차트 섹션 =====
        StudyStatisticsSection(
            record = state.dailyRecords,
            allTodos = state.todos,
            completedTodos = state.completedTodoItems
        )

        Spacer(modifier = Modifier.height(Dimens.Large))

        // ===== 시간대별 성과 히트맵 =====
        TimePerformanceHeatmap()
        Spacer(modifier = Modifier.height(Dimens.Large))

        // ===== 주차 인사이트 =====
        WeekInsights()
        Spacer(modifier = Modifier.height(Dimens.Large))
    }
}