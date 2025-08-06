package com.mukmuk.todori.ui.screen.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mukmuk.todori.data.remote.goal.Goal
import com.mukmuk.todori.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompletedGoalsViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {
    private val _completedGoals = MutableStateFlow<List<Goal?>>(emptyList())
    val goals: StateFlow<List<Goal?>> = _completedGoals

    fun loadCompletedGoals(uid: String) {
        viewModelScope.launch {
            val allGoals = repository.getGoals(uid)
            if (allGoals != null) {
                _completedGoals.value = allGoals.filter { it.completed }
            }
        }
    }
}
