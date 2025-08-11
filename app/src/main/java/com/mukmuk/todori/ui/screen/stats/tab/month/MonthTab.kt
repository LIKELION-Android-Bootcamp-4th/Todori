package com.mukmuk.todori.ui.screen.stats.tab.month

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.ui.screen.stats.component.MonthCard
import com.mukmuk.todori.ui.screen.stats.component.MonthProgress
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Dimens
import androidx.compose.runtime.mutableStateOf
import com.mukmuk.todori.ui.theme.Black
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun MonthTab(
    uid: String
) {
    var selectedMonth by remember {
        mutableStateOf(LocalDate.parse("2025-08-04"))
    }
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
            //왼쪽 화살표
            Row(
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                IconButton(onClick = {
                    selectedMonth = selectedMonth.minus(DatePeriod(months = 1))
                }) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Black
                    )
                }
            }

            //월 표시
            Row(
                modifier = Modifier.align(Alignment.Center)
            ) {
                Text(
                    "${selectedMonth.year}년 ${selectedMonth.monthNumber}월",
                    style = AppTextStyle.TitleSmall
                )
            }

            //오른쪽 화살표
            Row(
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                IconButton(onClick = {
                    selectedMonth = selectedMonth.plus(DatePeriod(months = 1))
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