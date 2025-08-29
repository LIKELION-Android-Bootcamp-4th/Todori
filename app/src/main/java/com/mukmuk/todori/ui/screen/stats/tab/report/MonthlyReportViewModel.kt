package com.mukmuk.todori.ui.screen.stats.tab.report

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mukmuk.todori.data.repository.DailyRecordRepository
import com.mukmuk.todori.data.repository.DayStatsRepository
import com.mukmuk.todori.data.repository.MonthStatRepository
import com.mukmuk.todori.data.repository.StudyTargetsRepository
import com.mukmuk.todori.ui.screen.stats.component.report.CategoryProgress
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
class MonthlyReportViewModel @Inject constructor(
    private val dailyRecordRepository: DailyRecordRepository,
    private val monthStatRepository: MonthStatRepository,
    private val studyTargetsRepository: StudyTargetsRepository,
    private val dayStatsRepository: DayStatsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MonthlyReportState())
    val state: StateFlow<MonthlyReportState> = _state.asStateFlow()

    fun loadMonthlyReport(uid: String, year: Int, month: Int) {
        viewModelScope.launch {
            updateState { copy(isLoading = true, error = null) }

            try {
                val monthStat = monthStatRepository.getMonthStat(uid, year, month)
                val lastMonthStat = monthStatRepository.getMonthStat(uid, year, month)
                val records = dailyRecordRepository.getRecordsByMonth(uid, year, month)
                val studyTargets = studyTargetsRepository.getStudyTargets(uid)
                val hourlySum = records.flatMap { it.hourlyMinutes.entries }
                    .groupBy({ it.key }, { it.value })
                    .mapValues { (_, list) -> list.sum() }

                val goldenHourRange = calculateGoldenHour(hourlySum)
                val goldenHourText = goldenHourRange?.let { (start, end) ->
                    if (start == end) "${start}시 집중"
                    else "${start}:00 ~${end}:00"
                }


                val streakDays = getMonthlyStreakDays(uid, year, month)
                val stats = dayStatsRepository.getStats(uid)
                val bestStreak = stats?.bestStreak ?: 0


                val categoryStat = monthStat?.categoryStats?.sortedByDescending { it.completionRate }
                    ?.take(3)?.map { stat ->
                        CategoryProgress(
                            name = stat.name,
                            completionRate = stat.completionRate
                        )
                    }

                val daysInMonth = YearMonth.of(year, month).lengthOfMonth()
                val daysInLastMonth = if (month == 1) {
                    YearMonth.of(year - 1, 12).lengthOfMonth()
                } else {
                    YearMonth.of(year, month - 1).lengthOfMonth()
                }
                val currentAvgMinutes = ((monthStat?.totalStudyTime ?: 0L) / daysInMonth / (1000 * 60)).toInt()

                val lastAvgMinutes = ((lastMonthStat?.totalStudyTime ?: 0L) / daysInLastMonth / (1000 * 60)).toInt()

                updateState {
                    copy(
                        isLoading = false,
                        totalStudyTimeHour = ((monthStat?.totalStudyTime ?: 0L) / (1000 * 60 * 60)).toInt(),
                        targetMonthStudyHour = (studyTargets?.monthlyMinutes ?: 0) / 60,
                        bestDay = monthStat?.bestDay?.date,
                        bestDayStudyTime = monthStat?.bestDay?.studyTime,
                        categoryStats = categoryStat ?: emptyList(),
                        goldenHourRange = goldenHourRange,
                        goldenHourText = goldenHourText,
                        streakDays = streakDays,
                        maxStreak = bestStreak,
                        currentTodoCompletionRate = monthStat?.todoCompletionRate ?: 0,
                        lastTodoCompletionRate = lastMonthStat?.todoCompletionRate ?: 0,
                        previousAvgStudyMinutes = lastAvgMinutes,
                        currentAvgStudyMinutes = currentAvgMinutes,
                    )
                }

            } catch (e: Exception) {
                updateState { copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun calculateGoldenHour(hourlyMinutes: Map<String, Long>): Pair<Int, Int>? {
        if (hourlyMinutes.isEmpty()) return null
        val (bestHourStr, _) = hourlyMinutes.maxByOrNull { it.value } ?: return null
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

    private fun updateState(update: MonthlyReportState.() -> MonthlyReportState) {
        _state.value = _state.value.update()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun getMonthlyStreakDays(uid: String, year: Int, month: Int): Int {
        val today = LocalDate.now()
        val targetYm = YearMonth.of(year, month)

        return if (today.year == year && today.monthValue == month) {
            val lastAvailableDate = today.minusDays(1)
            dayStatsRepository.getDayStat(uid, lastAvailableDate)?.streakCount ?: 0
        } else {
            val lastDayOfMonth = targetYm.atEndOfMonth()
            dayStatsRepository.getDayStat(uid, lastDayOfMonth)?.streakCount ?: 0
        }
    }


}
