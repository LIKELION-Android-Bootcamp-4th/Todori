package com.mukmuk.todori.ui.screen.stats.tab.day

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.hilt.navigation.compose.hiltViewModel
import com.mukmuk.todori.data.remote.dailyRecord.ReflectionV2
import com.mukmuk.todori.ui.screen.stats.component.day.CalendarCard
import com.mukmuk.todori.ui.screen.stats.component.day.DayPaceCard
import com.mukmuk.todori.ui.screen.stats.component.day.DayStatsCard
import com.mukmuk.todori.ui.screen.stats.component.day.GoldenHourCard
import com.mukmuk.todori.ui.screen.stats.component.day.LearningStreakCard
import com.mukmuk.todori.ui.screen.stats.component.day.ReflectionCard
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Red
import com.mukmuk.todori.util.parseReflection
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DayTab(
    uid: String,
    date: kotlinx.datetime.LocalDate,
    onDateChange: (kotlinx.datetime.LocalDate) -> Unit
) {
    val viewModel: DayViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(date) {
        viewModel.onDateSelected(
            LocalDate.of(date.year, date.monthNumber, date.dayOfMonth)
        )
        Log.d("DayTab: ","dayStat: ${state.dayStat}, stats: ${state.stats}")
    }

    LaunchedEffect(state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                focusManager.clearFocus(force = true)
            }
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.padding(Dimens.Medium)
            )
        }

        DayStatsCard(
            selectedDate = state.selectedDate,
            studyTimeMillis = state.studyTimeMillis,
            todos = state.todos
        )

        Spacer(modifier = Modifier.height(Dimens.Small))

        CalendarCard(
            record = state.monthRecords,
            selectedDate = state.selectedDate,
            onDateSelected = { picked ->
                viewModel.onDateSelected(picked)
                onDateChange(
                    kotlinx.datetime.LocalDate(
                        picked.year,
                        picked.monthValue,
                        picked.dayOfMonth
                    )
                )
            },
            onMonthChanged = { ym -> viewModel.onMonthChanged(ym) }
        )

        Spacer(modifier = Modifier.height(Dimens.Large))

        val weeklyPaceData = viewModel.getWeeklyPaceData()
        DayPaceCard(paceData = weeklyPaceData)
        Spacer(modifier = Modifier.height(Dimens.Large))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.Medium)
                .height(IntrinsicSize.Min)
        ) {
            LearningStreakCard(
                currentStreak = state.dayStat?.streakCount ?: 0,
                bestStreak = state.stats?.bestStreak ?: 0,
                qualifiedToday = state.dayStat?.qualified ?: false,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
            Spacer(Modifier.width(Dimens.Small))
            GoldenHourCard(
                startHour = state.goldenHourRange?.first,
                endHour = state.goldenHourRange?.second,
                completionDeltaPercent = state.completionPercent,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
        }
        Spacer(modifier = Modifier.height(Dimens.Large))

        ReflectionCard(
            initialText = state.currentReflectionPreview,
            onReflectionChanged = { newText ->
                val parsed = parseReflection(newText)
                viewModel.updateReflectionV2(
                    ReflectionV2(
                        good = parsed.good.takeIf { it.isNotBlank() },
                        improve = parsed.improve.takeIf { it.isNotBlank() },
                        blocker = parsed.blocker.takeIf { it.isNotBlank() }
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.Medium)
        )

        Spacer(modifier = Modifier.height(Dimens.Large))

        state.error?.let { error ->
            Text(
                text = error,
                style = AppTextStyle.BodyLarge.copy(color = Red),
                modifier = Modifier.padding(Dimens.Medium)
            )
        }
    }

    // 스낵바 호스트
    SnackbarHost(hostState = snackbarHostState)
}