package com.mukmuk.todori.ui.screen.mypage

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mukmuk.todori.data.remote.goal.Goal
import com.mukmuk.todori.data.repository.GoalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class CompletedGoalsViewModel @Inject constructor(
    private val repository: GoalRepository
) : ViewModel() {
    private val _completedGoals = MutableStateFlow<List<Goal?>>(emptyList())
    val goals: StateFlow<List<Goal?>> = _completedGoals

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    @RequiresApi(Build.VERSION_CODES.O)
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadCompletedGoals(uid: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val allGoals = repository.getGoals(uid)
                if (allGoals != null) {
                    val today = LocalDate.now()
                    _completedGoals.value = allGoals.filter { goal ->
                        runCatching {
                            val end = LocalDate.parse(goal.endDate, dateFormatter)
                            !end.isAfter(today)
                        }.getOrDefault(false)
                    }
                }
            } finally {
                _isLoading.value = false
            }
        }
    }
}
