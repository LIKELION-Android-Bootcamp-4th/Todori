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
        viewModel.loadMonthData(uid, selectedMonth.year, selectedMonth.monthNumber)
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
            avgStudyTimeMillis = state.avgStudyTimeMillis,
            totalStudyTimeMillis = state.totalStudyTimeMillis,
            bestWeek = state.bestWeek,
            worstWeek = state.worstWeek
        )
        Spacer(modifier = Modifier.height(Dimens.Large))
        SubjectAnalysisCard(
            subjects = state.categoryStats + state.goalStats
        )

        Spacer(modifier = Modifier.height(Dimens.Large))

        MonthHighlightCard(
            bestDay = state.bestDay.toString(),
            bestDayQuote = state.bestDayQuote.toString(),
            insights = state.insights
        )

        Spacer(modifier = Modifier.height(Dimens.Large))
    }
}