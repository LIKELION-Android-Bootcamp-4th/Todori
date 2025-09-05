package com.mukmuk.todori.ui.screen.stats.tab.day

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mukmuk.todori.data.remote.dailyRecord.ReflectionV2
import com.mukmuk.todori.data.repository.DailyRecordRepository
import com.mukmuk.todori.data.repository.DayStatsRepository
import com.mukmuk.todori.data.repository.StudyTargetsRepository
import com.mukmuk.todori.data.repository.TodoRepository
import com.mukmuk.todori.ui.screen.mypage.studytargets.WeeklyPaceData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
@RequiresApi(Build.VERSION_CODES.O)
class DayViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val todoRepository: TodoRepository,
    private val dailyRecordRepository: DailyRecordRepository,
    private val studyTargetsRepository: StudyTargetsRepository,
    private val dayStatsRepository: DayStatsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DayState())
    val state: StateFlow<DayState> = _state.asStateFlow()

    private var loadedMonth: YearMonth? = null

    init {
        refreshMonth()
        refreshSelected()
        observeStudyTargets()
    }

    private fun observeStudyTargets() = viewModelScope.launch {
        val uid = uidOrNull() ?: return@launch
        studyTargetsRepository.targetsFlow(uid).collect { targets ->
            updateState { copy(studyTargets = targets) }
        }
    }


    fun getWeeklyPaceData(): WeeklyPaceData {
        val targets = _state.value.studyTargets
        val weeklyTargetMinutes = targets?.weeklyMinutes ?: 0
        val selectedDate = _state.value.selectedDate

        val weekStart = selectedDate.minusDays(
            (selectedDate.dayOfWeek.value % 7).toLong()
        )
        val daysPassed = if (selectedDate.isBefore(LocalDate.now()) || selectedDate == LocalDate.now()) {
            (selectedDate.dayOfWeek.value % 7) + 1
        } else {
            (LocalDate.now().dayOfWeek.value % 7) + 1
        }

        val endDate = if (selectedDate.isAfter(LocalDate.now())) LocalDate.now() else selectedDate
        val actualCumulativeMinutes = calculateWeeklyActualTime(weekStart, endDate)

        val remainingMinutes = (weeklyTargetMinutes - actualCumulativeMinutes).coerceAtLeast(0)
        val remainingDays = (7 - daysPassed).coerceAtLeast(1)

        val requiredDailyMinutes = remainingMinutes / remainingDays

        val todayTargetMinutes = targets?.dailyMinutes ?: 0
        val todayActualMinutes = _state.value.studyTimeMillis / (1000 * 60)

        return WeeklyPaceData(
            weeklyTargetMinutes = weeklyTargetMinutes,
            requiredDailyMinutes = requiredDailyMinutes.toInt(),
            actualCumulativeMinutes = actualCumulativeMinutes.toInt(),
            todayTargetMinutes = todayTargetMinutes,
            todayActualMinutes = todayActualMinutes.toInt(),
            daysPassed = daysPassed
        )
    }

    private fun calculateWeeklyActualTime(weekStart: LocalDate, endDate: LocalDate): Long {
        val weekRecords = _state.value.monthRecords.filter { record ->
            val recordDate = LocalDate.parse(record.date)
            !recordDate.isBefore(weekStart) && !recordDate.isAfter(endDate)
        }

        return weekRecords.sumOf { record ->
            record.studyTimeMillis
        } / (1000 * 60)
    }

    fun onDateSelected(date: LocalDate) {
        if (date == _state.value.selectedDate) return

        updateState { copy(selectedDate = date) }
        refreshSelected()

        val now = LocalDate.now()
        if (date.year != now.year || date.monthValue != now.monthValue) {
            refreshMonth(date.year, date.monthValue)
        }
    }

    fun onMonthChanged(ym: YearMonth) {
        if (ym != loadedMonth) {
            refreshMonth(ym.year, ym.monthValue)
            loadedMonth = ym
        }
    }

    fun updateReflectionV2(reflectionV2: ReflectionV2) {
        val uid = uidOrNull() ?: return
        val date = _state.value.selectedDate

        updateState { copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                dailyRecordRepository.updateReflectionV2(uid, date, reflectionV2)
                refreshSelected()
            } catch (e: Exception) {
                updateState {
                    copy(
                        isLoading = false,
                        error = "회고 저장에 실패했습니다: ${e.message}"
                    )
                }
            }
        }
    }

    fun clearError() {
        updateState { copy(error = null) }
    }

    private fun uidOrNull(): String? = auth.currentUser?.uid ?: ""

    private fun updateState(update: DayState.() -> DayState) {
        _state.value = _state.value.update()
    }


    private fun refreshMonth(
        year: Int = LocalDate.now().year,
        month: Int = LocalDate.now().monthValue
    ) = viewModelScope.launch {
        val uid = uidOrNull() ?: return@launch

        updateState { copy(isLoading = true) }

        try {
            val records = dailyRecordRepository.getRecordsByMonth(uid, year, month)
            updateState {
                copy(
                    monthRecords = records,
                    isLoading = false,
                    error = null
                )
            }
        } catch (e: Exception) {
            updateState {
                copy(
                    isLoading = false,
                    error = "데이터 로딩에 실패했습니다: ${e.message}"
                )
            }
        }
    }

    private fun refreshSelected() = viewModelScope.launch {
        val uid = uidOrNull() ?: return@launch
        val date = _state.value.selectedDate

        updateState { copy(isLoading = true) }

        try {
            val todos = runCatching {
                todoRepository.getTodosByDate(uid, date)
            }.getOrElse { emptyList() }

            val record = runCatching {
                dailyRecordRepository.getRecordByDate(uid, date)
            }.getOrNull()

            val dayStat = runCatching { dayStatsRepository.getDayStat(uid, date) }.getOrNull()
            val stats = runCatching { dayStatsRepository.getStats(uid) }.getOrNull()

            val goldenHourRange = record?.hourlyMinutes?.let { hourly ->
                calculateGoldenHour(hourly)
            }

            val goldenHourText = goldenHourRange?.let { (start, end) ->
                if (start == end) "${start}시 집중"
                else "${start}:00 ~${end}:00"
            }

            updateState {
                copy(
                    todos = todos,
                    selectedRecord = record,
                    dayStat = dayStat,
                    stats = stats,
                    isLoading = false,
                    goldenHourRange = goldenHourRange,
                    goldenHour = goldenHourText,
                    error = null
                )
            }
        } catch (e: Exception) {
            updateState {
                copy(
                    isLoading = false,
                    error = "데이터 로딩에 실패했습니다: ${e.message}"
                )
            }
        }
    }


    private fun calculateGoldenHour(hourlyMinutes: Map<String, Long>): Pair<Int, Int>? {
        if (hourlyMinutes.isEmpty()) return null

        val (bestHourStr, bestHourValue) = hourlyMinutes.maxByOrNull { it.value } ?: return null

        val bestHour = bestHourStr.toInt()
        val ranges = listOf(
            (bestHour - 2)..bestHour,
            (bestHour - 1)..(bestHour + 1),
            bestHour..(bestHour + 2)
        )

        var bestRange = ranges.first()
        var maxSum = 0L

        for (range in ranges) {
            val currentSum = range.sumOf { hour ->
                hourlyMinutes[hour.toString()] ?: 0L
            }
            if (currentSum > maxSum) {
                maxSum = currentSum
                bestRange = range
            }
        }

        val otherValue = maxSum - bestHourValue
        if (bestHourValue >= otherValue * 2) {
            return (bestHour to bestHour)
        }

        var start = bestRange.first
        var end = bestRange.last
        while (end > start && (hourlyMinutes[end.toString()] ?: 0L) == 0L) {
            end--
        }

        return (start to end)
    }



}
