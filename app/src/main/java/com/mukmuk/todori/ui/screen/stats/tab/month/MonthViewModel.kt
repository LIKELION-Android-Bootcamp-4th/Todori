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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MonthViewModel @Inject constructor(
    private val todoStatsRepository: TodoStatsRepository,
    private val goalStatsRepository: GoalStatsRepository,
    private val studyStatsRepository: StudyStatsRepository,
    private val dailyRecordRepository: DailyRecordRepository
) : ViewModel() {

    private val _completedTodos = MutableStateFlow(0)
    val completedTodos: StateFlow<Int> = _completedTodos.asStateFlow()

    private val _totalTodos = MutableStateFlow(0)
    val totalTodos: StateFlow<Int> = _totalTodos.asStateFlow()

    private val _completedGoals = MutableStateFlow(0)
    val completedGoals: StateFlow<Int> = _completedGoals.asStateFlow()

    private val _totalGoals = MutableStateFlow(0)
    val totalGoals: StateFlow<Int> = _totalGoals

    private val _completedStudyTodos = MutableStateFlow(0)
    val completedStudyTodos: StateFlow<Int> = _completedStudyTodos.asStateFlow()

    private val _totalStudyTodos = MutableStateFlow(0)
    val totalStudyTodos: StateFlow<Int> = _totalStudyTodos

    private val _avgStudyTimeMillis = MutableStateFlow(0L)
    val avgStudyTimeMillis: StateFlow<Long> = _avgStudyTimeMillis.asStateFlow()

    private val _totalStudyTimeMillis = MutableStateFlow(0L)
    val totalStudyTimeMillis: StateFlow<Long> = _totalStudyTimeMillis.asStateFlow()
    fun loadTodoStats(uid: String, year: Int, month: Int) {
        viewModelScope.launch {
            _completedTodos.value = todoStatsRepository.getCompletedTodoCount(uid, year, month)
            _totalTodos.value = todoStatsRepository.getTotalTodoCount(uid, year, month)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadGoalStats(uid: String, year: Int, month: Int) {
        viewModelScope.launch {
            val completed = goalStatsRepository.getCompletedGoalCount(uid, year, month)
            val total = goalStatsRepository.getTotalGoalCount(uid, year, month)
            _completedGoals.value = completed
            _totalGoals.value = total
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadStudyTodosStats(uid: String, year: Int, month: Int) {
        viewModelScope.launch {
            val completed = studyStatsRepository.getCompletedStudyTodosCount(uid, year, month)
            val total = studyStatsRepository.getTotalStudyTodosCount(uid, year, month)
            _completedStudyTodos.value = completed
            _totalStudyTodos.value = total
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadStudyTimeStats(uid: String, year: Int, month: Int) {
        viewModelScope.launch {
            val total = dailyRecordRepository.getTotalStudyTimeMillis(uid, year, month)
            val avg = dailyRecordRepository.getAverageStudyTimeMillis(uid, year, month)
            _totalStudyTimeMillis.value = total
            _avgStudyTimeMillis.value = avg
        }
    }
}
