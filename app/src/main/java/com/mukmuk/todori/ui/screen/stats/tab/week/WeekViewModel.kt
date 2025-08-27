package com.mukmuk.todori.ui.screen.stats.tab.week

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mukmuk.todori.data.repository.DailyRecordRepository
import com.mukmuk.todori.data.repository.StudyTargetsRepository
import com.mukmuk.todori.data.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
@RequiresApi(Build.VERSION_CODES.O)
class WeekViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val dailyRecordRepository: DailyRecordRepository,
    private val studyTargetsRepository: StudyTargetsRepository
) : ViewModel() {
    private val _state = MutableStateFlow(WeekState())
    val state: StateFlow<WeekState> = _state.asStateFlow()


    fun getWeekRange(date: LocalDate): List<LocalDate> {
        val dayOfWeek = date.dayOfWeek.value % 7
        val sunday = date.minusDays(dayOfWeek.toLong())
        return (0..6).map { sunday.plusDays(it.toLong()) }
    }

    fun loadWeekTodos(uid: String, date: LocalDate) = viewModelScope.launch {
        try {
            val range = getWeekRange(date)
            val sunday = range.first()
            val saturday = range.last()

            val weeklyTodos = todoRepository.getTodosByWeek(uid, sunday, saturday)
            val completed = weeklyTodos.filter { it.completed }

            _state.update {
                it.copy(
                    todos = weeklyTodos,
                    completedTodoItems = completed,
                    totalTodos = weeklyTodos.size,
                    completedTodos = completed.size
                )
            }
            updateInsights()
        } catch (e: Exception) {
            Log.d("WeekViewModel", "주간 투두 불러오기 실패 : ${e.message}")
        }
    }

    fun loadWeekStudy(uid: String, date: LocalDate) = viewModelScope.launch {
        try {
            val range = getWeekRange(date)
            val sunday = range.first()
            val saturday = range.last()

            val records = dailyRecordRepository.getRecordsByWeek(uid, sunday, saturday)

            val studied = records.filter { it.studyTimeMillis > 0L }
            val totalSec = studied.sumOf { it.studyTimeMillis }
            val avgSec = if (studied.isNotEmpty()) totalSec / studied.size else 0L

            _state.update {
                it.copy(
                    dailyRecords = records,
                    totalStudyTimeMillis = totalSec * 1000,
                    avgStudyTimeMillis = avgSec * 1000
                )
            }
            updateInsights()
        } catch (e: Exception) {
            Log.d("WeekViewModel", "주간 공부기록 불러오기 실패 : ${e.message}")
        }
    }

    fun loadStudyTargets(uid: String) = viewModelScope.launch {
        try {
            val targets = studyTargetsRepository.getStudyTargets(uid)
            _state.update {
                it.copy(studyTargets = targets)
            }
            updateInsights()
        } catch (e: Exception) {
            Log.d("WeekViewModel", "스터디 목표 불러오기 실패 : ${e.message}")
        }
    }

    private fun updateInsights() {
        val records = _state.value.dailyRecords
        val todos = _state.value.todos
        val completed = _state.value.completedTodoItems
        val target = _state.value.studyTargets?.dailyMinutes ?: 0

        val maxRecord = records.maxByOrNull { it.studyTimeMillis }
        val productiveDay = maxRecord?.date?.let { date ->
            LocalDate.parse(date).dayOfWeek
                .getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.KOREAN)
                .take(3)
        } ?: ""

        val productiveDuration = formatDuration(maxRecord?.studyTimeMillis ?: 0L)

        val completionRate = if (todos.isNotEmpty()) {
            (completed.size.toFloat() / todos.size * 100).toInt()
        } else 0

        val allHourly = records.flatMap { it.hourlyMinutes.entries }
        val bestHour = allHourly.maxByOrNull { it.value }
        val bestHourKey = bestHour?.key?.toIntOrNull()

        val bestTimeSlot = when (bestHourKey) {
            in 6..8 -> "06-09시"
            in 9..11 -> "09-12시"
            in 12..14 -> "12-15시"
            in 15..17 -> "15-18시"
            in 18..20 -> "18-21시"
            in 21..23 -> "21-24시"
            in 0..5 -> "00-06시"
            else -> bestHourKey?.let { "${it}시" } ?: ""
        }

        val bestTimeSlotValues = if (bestHourKey != null) {
            val slotRange = when(bestHourKey) {
                in 6..8 -> 6..8
                in 9..11 -> 9..11
                in 12..14 -> 12..14
                in 15..17 -> 15..17
                in 18..20 -> 18..20
                in 21..23 -> 21..23
                else -> bestHourKey..bestHourKey
            }
            allHourly.filter { it.key.toIntOrNull() in slotRange }.map { it.value }
        } else emptyList()

        val maxValue = allHourly.maxOfOrNull { it.value } ?: 1L
        val bestTimeSlotRate = if (bestTimeSlotValues.isNotEmpty()) {
            (bestTimeSlotValues.average() / maxValue * 100).toInt()
        } else 0

        val plannedHours = target / 60f
        val actualHours = records.map { it.studyTimeMillis / 1000f / 60f / 60f }
        val overTarget = actualHours.count { it >= plannedHours }
        val planAchievement = (overTarget.toFloat() / 7f * 100).toInt()

        _state.update {
            it.copy(
                insights = WeekInsightsData(
                    productiveDay,
                    productiveDuration,
                    completionRate,
                    bestTimeSlot,
                    bestTimeSlotRate,
                    planAchievement
                )
            )
        }
    }

    private fun formatDuration(millis: Long): String {
        val minutes = (millis / 1000 / 60).toInt()
        val hours = minutes / 60
        val remain = minutes % 60
        return "${hours}시간 ${remain}분"
    }

}