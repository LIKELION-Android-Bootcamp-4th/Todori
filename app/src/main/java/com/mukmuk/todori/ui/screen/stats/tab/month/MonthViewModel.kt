package com.mukmuk.todori.ui.screen.stats.tab.month

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mukmuk.todori.data.remote.stat.MonthStat
import com.mukmuk.todori.data.remote.stat.WeekStat
import com.mukmuk.todori.data.repository.DailyRecordRepository
import com.mukmuk.todori.data.repository.GoalStatsRepository
import com.mukmuk.todori.data.repository.MonthStatRepository
import com.mukmuk.todori.data.repository.StudyStatsRepository
import com.mukmuk.todori.data.repository.StudyTargetsRepository
import com.mukmuk.todori.data.repository.TodoStatsRepository
import com.mukmuk.todori.ui.screen.stats.component.month.SubjectProgress
import com.mukmuk.todori.ui.theme.GoalPrimary
import com.mukmuk.todori.ui.theme.UserPrimary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.Locale
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class MonthViewModel @Inject constructor(
    private val todoStatsRepository: TodoStatsRepository,
    private val goalStatsRepository: GoalStatsRepository,
    private val studyStatsRepository: StudyStatsRepository,
    private val dailyRecordRepository: DailyRecordRepository,
    private val studyTargetsRepository: StudyTargetsRepository,
    private val monthStatRepository: MonthStatRepository
) : ViewModel() {

    private val _monthState = MutableStateFlow(MonthState())
    val monthState: StateFlow<MonthState> = _monthState.asStateFlow()

    fun loadTodoStats(uid: String, year: Int, month: Int) {
        viewModelScope.launch {
            val completed = todoStatsRepository.getCompletedTodoCount(uid, year, month)
            val total = todoStatsRepository.getTotalTodoCount(uid, year, month)
            _monthState.update {
                it.copy(
                    completedTodos = completed,
                    totalTodos = total
                )
            }
        }
    }

    fun loadGoalStats(uid: String, year: Int, month: Int) {
        viewModelScope.launch {
            val completed = goalStatsRepository.getCompletedGoalCount(uid, year, month)
            val total = goalStatsRepository.getTotalGoalCount(uid, year, month)
            _monthState.update {
                it.copy(
                    completedGoals = completed,
                    totalGoals = total
                )
            }
        }
    }

    fun loadStudyTodosStats(uid: String, year: Int, month: Int) {
        viewModelScope.launch {
            val completed = studyStatsRepository.getCompletedStudyTodosCount(uid, year, month)
            val total = studyStatsRepository.getTotalStudyTodosCount(uid, year, month)
            _monthState.update {
                it.copy(
                    completedStudyTodos = completed,
                    totalStudyTodos = total
                )
            }
        }
    }

    fun loadStudyTimeStats(uid: String, year: Int, month: Int) {
        viewModelScope.launch {
            val total = dailyRecordRepository.getTotalStudyTimeMillis(uid, year, month)
            val avg = dailyRecordRepository.getAverageStudyTimeMillis(uid, year, month)
            _monthState.update {
                it.copy(
                    totalStudyTimeMillis = total,
                    avgStudyTimeMillis = avg
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun updateWeeklyStats(uid: String, year: Int, month: Int) {
        val records = dailyRecordRepository.getRecordsByMonth(uid, year, month)

        val weekFields = WeekFields.of(Locale.KOREAN)

        val start = LocalDate.of(year, month, 1)
        val end = start.plusMonths(1).minusDays(1)
        val totalWeeks = end.get(weekFields.weekOfMonth())

        val grouped = records.groupBy { record ->
            val localDate = LocalDate.parse(record.date)
            localDate.get(weekFields.weekOfMonth())
        }

        val weekStats = (1..totalWeeks).map { week ->
            val recs = grouped[week].orEmpty()
            WeekStat(
                label = "${month}월 ${week}주차",
                totalStudyTimeMillis = recs.sumOf { it.studyTimeMillis }
            )
        }

        val bestWeek = weekStats.maxByOrNull { it.totalStudyTimeMillis }
        val worstWeek = weekStats.minByOrNull { it.totalStudyTimeMillis }

        _monthState.update {
            it.copy(
                bestWeek = bestWeek,
                worstWeek = worstWeek
            )
        }
    }




    fun loadMonthStats(uid: String, year: Int, month: Int) {
        viewModelScope.launch {
            val stat = monthStatRepository.getMonthStat(uid, year, month)

            val bestDayDate = stat?.bestDay?.date
            val bestDayQuote = if (bestDayDate != null) {
                val localDate = LocalDate.parse(bestDayDate)
                val record = dailyRecordRepository.getRecordByDate(uid, localDate)
                record?.reflectionV2?.good ?: record?.reflection ?: ""
            } else ""

            stat?.let {
                _monthState.update {
                    it.copy(
                        categoryStats = stat.categoryStats.map { c ->
                            SubjectProgress(
                                name = c.name,
                                completionRate = c.completionRate,
                                color = UserPrimary
                            )
                        },
                        goalStats = stat.goalStats.map { g ->
                            SubjectProgress(
                                name = g.title,
                                completionRate = g.completionRate,
                                color = GoalPrimary
                            )
                        },
                        bestDay = bestDayDate,
                        bestDayQuote = bestDayQuote,
                    )
                }
                updateInsights(uid, stat)
                updateWeeklyStats(uid, year, month)
            }
        }
    }

    private suspend fun updateInsights(uid: String, monthStat: MonthStat) {
        val insights = mutableListOf<String>()

        val allSubjects = monthStat.categoryStats.map { it.name to it.completionRate } +
                monthStat.goalStats.map { it.title to it.completionRate }

        val best = allSubjects.maxByOrNull { it.second }
        val worst = allSubjects.minByOrNull { it.second }

        best?.let {
            insights.add("이번 달 가장 높은 성과는 '${it.first}' (${it.second}%) 였어요")
        }
        worst?.let {
            insights.add("'${it.first}' 주제는 상대적으로 낮은 성과를 보였어요 (${it.second}%)")
        }

        val studyTargets = studyTargetsRepository.getStudyTargets(uid)
        val targetHours = (studyTargets?.monthlyMinutes ?: 0) / 60

        val actualHours = monthStat.totalStudyTime / (1000 * 60 * 60)
        if (targetHours > 0) {
            val percent = (actualHours.toFloat() / targetHours * 100).toInt()
            insights.add("계획 대비 실제 학습 시간이 ${percent}% 달성되었어요")
        }

        _monthState.update {
            it.copy(insights = insights)
        }
    }


}
