package com.mukmuk.todori.ui.screen.stats.tab.report

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mukmuk.todori.data.remote.stat.MonthStat
import com.mukmuk.todori.data.repository.DailyRecordRepository
import com.mukmuk.todori.data.repository.MonthStatRepository
import com.mukmuk.todori.data.repository.StudyTargetsRepository
import com.mukmuk.todori.data.repository.TodoStatsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@RequiresApi(Build.VERSION_CODES.O)
class MonthlyReportViewModel @Inject constructor(
    private val dailyRecordRepository: DailyRecordRepository,
    private val monthStatRepository: MonthStatRepository,
    private val studyTargetsRepository: StudyTargetsRepository,
    private val todoStatsRepository: TodoStatsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MonthlyReportState())
    val state: StateFlow<MonthlyReportState> = _state.asStateFlow()

    fun loadMonthlyReport(uid: String, year: Int, month: Int) {
        viewModelScope.launch {
            updateState { copy(isLoading = true, error = null) }

            try {
                val monthStat = monthStatRepository.getMonthStat(uid, year, month)

                val records = dailyRecordRepository.getRecordsByMonth(uid, year, month)
                val hourlySum = records.flatMap { it.hourlyMinutes.entries }
                    .groupBy({ it.key }, { it.value })
                    .mapValues { (_, list) -> list.sum() }

                val goldenHourRange = calculateGoldenHour(hourlySum)
                val goldenHourText = goldenHourRange?.let { (start, end) ->
                    if (start == end) "${start}시 집중"
                    else "${start}:00 ~${end}:00"
                }

                updateState {
                    copy(
                        year = year,
                        month = month,
                        isLoading = false,
                        totalStudyTimeMillis = monthStat?.totalStudyTime ?: 0L,
                        avgStudyTimeMillis = (monthStat?.totalStudyTime ?: 0L) / 30,
                        completedTodos = monthStat?.completedTodos ?: 0,
                        totalTodos = monthStat?.totalTodos ?: 0,
                        bestDay = monthStat?.bestDay?.date,
                        bestDayStudyTime = monthStat?.bestDay?.studyTime?.toLongOrNull() ?: 0L,
                        bestWeekLabel = "TODO: 주차라벨",
                        bestWeekStudyTime = 0L, // 동일
                        goldenHourRange = goldenHourRange,
                        goldenHourText = goldenHourText,
                        insights = buildInsights(monthStat)
                    )
                }

            } catch (e: Exception) {
                updateState { copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun calculateGoldenHour(hourlyMinutes: Map<String, Long>): Pair<Int, Int>? {
        if (hourlyMinutes.isEmpty()) return null
        val (bestHourStr, bestHourValue) = hourlyMinutes.maxByOrNull { it.value } ?: return null
        val bestHour = bestHourStr.toInt()
        val ranges = listOf(
            (bestHour - 1)..(bestHour + 1),
            bestHour..(bestHour + 2)
        )
        var bestRange = ranges.first()
        var maxSum = 0L
        for (range in ranges) {
            val currentSum = range.sumOf { hour -> hourlyMinutes[hour.toString()] ?: 0L }
            if (currentSum > maxSum) {
                maxSum = currentSum
                bestRange = range
            }
        }
        return bestRange.first to bestRange.last
    }

    private fun buildInsights(monthStat: MonthStat?): List<String> {
        if (monthStat == null) return emptyList()
        val insights = mutableListOf<String>()
        val best = monthStat.categoryStats.maxByOrNull { it.completionRate }
        val worst = monthStat.categoryStats.minByOrNull { it.completionRate }
        best?.let { insights.add("이번 달 최고 성과는 ${it.name} (${it.completionRate}%)") }
        worst?.let { insights.add("${it.name} 주제는 성과가 낮았어요 (${it.completionRate}%)") }
        return insights
    }

    private fun updateState(update: MonthlyReportState.() -> MonthlyReportState) {
        _state.value = _state.value.update()
    }
}
