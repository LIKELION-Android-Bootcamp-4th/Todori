package com.mukmuk.todori.ui.screen.stats

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mukmuk.todori.ui.screen.stats.component.report.CompletionRateCard
import com.mukmuk.todori.ui.screen.stats.component.report.EncouragementCard
import com.mukmuk.todori.ui.screen.stats.component.report.EnduranceCard
import com.mukmuk.todori.ui.screen.stats.component.report.GoalProgressCard
import com.mukmuk.todori.ui.screen.stats.component.report.LegendaryDayCard
import com.mukmuk.todori.ui.screen.stats.component.report.MonthHeroCard
import com.mukmuk.todori.ui.screen.stats.component.report.MonthlyReportHeader
import com.mukmuk.todori.ui.screen.stats.component.report.StudyGoalCard
import com.mukmuk.todori.ui.screen.stats.component.report.StudyStreakCard
import com.mukmuk.todori.ui.screen.stats.tab.report.MonthlyReportViewModel
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.DarkGray
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Red
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
        when {
            state.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            state.error != null -> {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(Dimens.Medium),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "데이터를 불러오는 중 오류가 발생했습니다.",
                        style = AppTextStyle.BodyLarge,
                        color = Red
                    )
                    Spacer(Modifier.height(Dimens.Small))
                    Text(
                        text = state.error ?: "",
                        style = AppTextStyle.BodySmallNormal,
                        color = DarkGray
                    )
                    Spacer(Modifier.height(Dimens.Medium))
                    Button(onClick = {
                        viewModel.loadMonthlyReport(uid, date.year, date.monthNumber)
                    }) {
                        Text("다시 시도")
                    }
                }
            }

            else -> {
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
                        date = if (state.bestDay !=null) {
                            state.bestDay.toString().takeLast(2)
                        } else null,
                        studyTime = state.bestDayStudyTime
                    )
                    Spacer(modifier = Modifier.height(Dimens.Medium))

                    MonthHeroCard(
                        topCategories = state.categoryStats
                    )
                    Spacer(modifier = Modifier.height(Dimens.Medium))

                    CompletionRateCard(
                        previousRate = state.lastTodoCompletionRate,
                        currentRate = state.currentTodoCompletionRate,
                        improvement = state.improvement
                    )
                    Spacer(modifier = Modifier.height(Dimens.Medium))

                    GoalProgressCard(
                        currentTime = state.totalStudyTimeHour,
                        goalTime = state.targetMonthStudyHour,
                        leftTime = state.leftTime
                    )
                    Spacer(modifier = Modifier.height(Dimens.Medium))

                    EnduranceCard(
                        previousAverage = state.previousAvgStudyMinutes,
                        currentAverage = state.currentAvgStudyMinutes,
                        improvement = state.enduranceImprovement
                    )
                    Spacer(modifier = Modifier.height(Dimens.Medium))

                    EncouragementCard(month = date.monthNumber)
                    Spacer(modifier = Modifier.height(Dimens.XXLarge))
                }
            }
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
