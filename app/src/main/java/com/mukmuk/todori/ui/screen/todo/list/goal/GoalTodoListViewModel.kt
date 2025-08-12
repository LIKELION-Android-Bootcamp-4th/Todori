package com.mukmuk.todori.ui.screen.todo.list.goal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mukmuk.todori.data.repository.GoalRepository
import com.mukmuk.todori.data.repository.GoalTodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class GoalTodoListViewModel @Inject constructor(
    private val goalRepository: GoalRepository,
    private val goalTodoRepository: GoalTodoRepository
) : ViewModel() {

    private val _state = MutableStateFlow(GoalTodoListState())
    val state: StateFlow<GoalTodoListState> = _state

    fun loadGoalsWithTodos(uid: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val goals = goalRepository.getGoals(uid)
                val goalTodoMap = goals.associate { goal ->
                    goal.goalId to goalTodoRepository.getGoalTodosByGoalId(uid, goal.goalId)
                }
                _state.value = _state.value.copy(isLoading = false, goals = goals, goalTodosMap = goalTodoMap)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }
}
