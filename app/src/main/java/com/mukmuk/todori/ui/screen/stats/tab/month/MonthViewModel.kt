package com.mukmuk.todori.ui.screen.stats.tab.month

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mukmuk.todori.data.repository.GoalStatsRepository
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
    private val goalStatsRepository: GoalStatsRepository
) : ViewModel() {

    private val _completedTodos = MutableStateFlow(0)
    val completedTodos: StateFlow<Int> = _completedTodos.asStateFlow()

    private val _totalTodos = MutableStateFlow(0)
    val totalTodos: StateFlow<Int> = _totalTodos.asStateFlow()

    private val _completedGoals = MutableStateFlow(0)
    val completedGoals: StateFlow<Int> = _completedGoals.asStateFlow()


    fun loadTodoStats(uid: String, year: Int, month: Int) {
        viewModelScope.launch {
            _completedTodos.value = todoStatsRepository.getCompletedTodoCount(uid, year, month)
            _totalTodos.value = todoStatsRepository.getTotalTodoCount(uid, year, month)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadGoalStats(uid: String, year: Int, month: Int) {
        viewModelScope.launch {
            _completedGoals.value = goalStatsRepository.getCompletedGoalCount(uid, year, month)
        }
    }
}
