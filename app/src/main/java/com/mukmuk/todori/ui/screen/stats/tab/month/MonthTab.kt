package com.mukmuk.todori.ui.screen.stats.tab.month

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mukmuk.todori.ui.screen.stats.component.MonthCard
import com.mukmuk.todori.ui.screen.stats.component.MonthProgress
import com.mukmuk.todori.ui.screen.stats.component.month.MonthHighlightCard
import com.mukmuk.todori.ui.screen.stats.component.month.SubjectAnalysisCard
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.Dimens
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MonthTab(
    uid: String,
    date: LocalDate,
    onDateChange: (LocalDate) -> Unit
) {
    val selectedMonth = LocalDate(date.year, date.monthNumber, 1)

    val viewModel: MonthViewModel = hiltViewModel()
    val state by viewModel.monthState.collectAsState()

    LaunchedEffect(selectedMonth) {
        viewModel.loadTodoStats(
            uid = uid,
            year = selectedMonth.year,
            month = selectedMonth.monthNumber
        )
        viewModel.loadGoalStats(
            uid = uid,
            year = selectedMonth.year,
            month = selectedMonth.monthNumber
        )
        viewModel.loadStudyTodosStats(
            uid = uid,
            year = selectedMonth.year,
            month = selectedMonth.monthNumber
        )
        viewModel.loadStudyTimeStats(
            uid = uid,
            year = selectedMonth.year,
            month = selectedMonth.monthNumber
        )
        viewModel.loadMonthStats(
            uid = uid,
            year = selectedMonth.year,
            month = selectedMonth.monthNumber
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())

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
                    onDateChange(selectedMonth.minus(DatePeriod(months = 1)))
                }) {
                    Icon(
                        Icons.Default.ArrowBack,
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
                    "${selectedMonth.year}년 ${selectedMonth.monthNumber}월",
                    style = AppTextStyle.TitleSmall
                )
            }

            Row(
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                IconButton(onClick = {
                    onDateChange(selectedMonth.plus(DatePeriod(months = 1)))
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

        MonthCard(
            completedTodos = state.completedTodos,
            totalTodos = state.totalTodos,
            completedGoals = state.completedGoals,
            avgStudyTimeMillis = state.avgStudyTimeMillis,
            totalStudyTimeMillis = state.totalStudyTimeMillis
        )
        Spacer(modifier = Modifier.height(Dimens.Large))
        SubjectAnalysisCard(
            subjects = state.categoryStats + state.goalStats
        )

        Spacer(modifier = Modifier.height(Dimens.Large))

        MonthHighlightCard(
            bestKeywords = listOf("집중", "성취감", "도전", "성장", "만족"),
            bestDay = "7월 15일",
            bestDayQuote = "알고리즘 문제를 연속으로 해결해서 정말 뿌듯했다!",
            insights = listOf(
                "수학 과목의 집중시간이 감저한 원포들이 늘어요 (65%)",
                "영어는 목표 집중직으로 처히 더 효과적이어요 (92% 완료율)",
                "7월 3주차가 최고의 성과를 보였어요 (32.5시간)",
                "'성취감'이 가장 자주 나타난 최고 키워드예요"
            )
        )

        Spacer(modifier = Modifier.height(Dimens.Large))
        MonthProgress(
            completedTodos = state.completedTodos,
            totalTodos = state.totalTodos,
            completedGoals = state.completedGoals,
            totalGoals = state.totalGoals,
            completedStudyTodos = state.completedStudyTodos,
            totalStudyTodos = state.totalStudyTodos
        )
    }
}