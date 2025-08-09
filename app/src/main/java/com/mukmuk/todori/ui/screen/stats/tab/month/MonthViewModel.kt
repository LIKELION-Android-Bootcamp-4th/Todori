package com.mukmuk.todori.ui.screen.stats.tab.month

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mukmuk.todori.data.repository.DailyRecordRepository
import com.mukmuk.todori.data.repository.GoalStatsRepository
import com.mukmuk.todori.data.repository.StudyStatsRepository
import com.mukmuk.todori.data.repository.TodoStatsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MonthViewModel @Inject constructor(
    private val todoStatsRepository: TodoStatsRepository,
    private val goalStatsRepository: GoalStatsRepository,
    private val studyStatsRepository: StudyStatsRepository,
    private val dailyRecordRepository: DailyRecordRepository
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

    @RequiresApi(Build.VERSION_CODES.O)
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

    @RequiresApi(Build.VERSION_CODES.O)
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

    @RequiresApi(Build.VERSION_CODES.O)
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
}
