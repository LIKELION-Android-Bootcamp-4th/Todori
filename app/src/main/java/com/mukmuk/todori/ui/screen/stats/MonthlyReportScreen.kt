package com.mukmuk.todori.ui.screen.stats

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mukmuk.todori.ui.screen.stats.component.report.CategoryProgress
import com.mukmuk.todori.ui.screen.stats.component.report.CompletionRateCard
import com.mukmuk.todori.ui.screen.stats.component.report.EncouragementCard
import com.mukmuk.todori.ui.screen.stats.component.report.EnduranceCard
import com.mukmuk.todori.ui.screen.stats.component.report.GoalProgressCard
import com.mukmuk.todori.ui.screen.stats.component.report.LegendaryDayCard
import com.mukmuk.todori.ui.screen.stats.component.report.MonthHeroCard
import com.mukmuk.todori.ui.screen.stats.component.report.MonthlyReportHeader
import com.mukmuk.todori.ui.screen.stats.component.report.StudyGoalCard
import com.mukmuk.todori.ui.screen.stats.component.report.StudyStreakCard
import com.mukmuk.todori.ui.screen.stats.component.report.WeeklyCompletionCard
import com.mukmuk.todori.ui.screen.stats.component.report.WeeklyRate
import com.mukmuk.todori.ui.screen.stats.tab.report.MonthlyReportViewModel
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.White
import kotlinx.datetime.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MonthlyReportScreen(
    uid: String,
    date: LocalDate,
    onBackClick: () -> Unit
) {
    val viewModel: MonthlyReportViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()


    LaunchedEffect(date) {
        viewModel.loadMonthlyReport(uid, date.year, date.monthNumber)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(150.dp))

            StudyGoalCard(
                startHour = state.goldenHourRange?.first ?: 0,
                endHour = state.goldenHourRange?.second ?: 0
            )
            Spacer(modifier = Modifier.height(Dimens.Medium))

            StudyStreakCard(
                streakDays = state.streakDays,
                maxStreak = state.maxStreak
            )
            Spacer(modifier = Modifier.height(Dimens.Medium))

            LegendaryDayCard(
                date = state.bestDay.toString().takeLast(2),
                studyTime = state.bestDayStudyTime.toString()
            )
            Spacer(modifier = Modifier.height(Dimens.Medium))

            MonthHeroCard(
                topCategories = listOf(
                    CategoryProgress("자료구조", 89, "11h"),
                    CategoryProgress("알고리즘", 76, "8.5h"),
                    CategoryProgress("리액트", 65, "7.2h")
                )
            )
            Spacer(modifier = Modifier.height(Dimens.Medium))

            CompletionRateCard(previousRate = 32, currentRate = 45, improvement = 13)
            Spacer(modifier = Modifier.height(Dimens.Medium))

            WeeklyCompletionCard(
                weeklyRates = listOf(
                    WeeklyRate("월", 65),
                    WeeklyRate("화", 78),
                    WeeklyRate("수", 97),
                    WeeklyRate("목", 83),
                    WeeklyRate("금", 72),
                    WeeklyRate("토", 56),
                    WeeklyRate("일", 48)
                )
            )
            Spacer(modifier = Modifier.height(Dimens.Medium))

            GoalProgressCard(currentTime = 47, goalTime = 50, timeLeft = 3)
            Spacer(modifier = Modifier.height(Dimens.Medium))

            EnduranceCard(previousAverage = 32, currentAverage = 41, improvement = 9)
            Spacer(modifier = Modifier.height(Dimens.Medium))

            EncouragementCard()
            Spacer(modifier = Modifier.height(Dimens.XXLarge))
        }

        MonthlyReportHeader(
            month = "${date.monthNumber}월",
            onBackClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .background(White.copy(alpha = 0.7f))
        )
    }
}
