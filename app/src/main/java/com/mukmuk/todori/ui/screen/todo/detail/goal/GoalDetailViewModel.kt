package com.mukmuk.todori.ui.screen.todo.detail.goal

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.mukmuk.todori.data.remote.goal.GoalTodo
import com.mukmuk.todori.data.repository.GoalRepository
import com.mukmuk.todori.data.repository.GoalTodoRepository
import com.mukmuk.todori.widget.goaldaycount.DayCountWidgetReceiver
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoalDetailViewModel @Inject constructor(
    private val goalRepository: GoalRepository,
    private val goalTodoRepository: GoalTodoRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _state = MutableStateFlow(GoalDetailState())
    val state: StateFlow<GoalDetailState> = _state

    fun loadGoalDetail(uid: String, goalId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val goal = goalRepository.getGoalById(uid, goalId)
                val todos = goalTodoRepository.getGoalTodosByGoalId(uid, goalId)
                _state.value = _state.value.copy(
                    isLoading = false,
                    goal = goal,
                    todos = todos,
                    error = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun toggleGoalTodoCompleted(uid: String, goalTodo: GoalTodo) {
        viewModelScope.launch {
            val updated = goalTodo.copy(completed = !goalTodo.completed)
            try {
                goalTodoRepository.updateGoalTodo(uid, updated)

                _state.value = _state.value.copy(
                    todos = _state.value.todos.map {
                        if (it.goalTodoId == updated.goalTodoId) updated else it
                    }
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    fun deleteGoalTodo(uid: String, goalTodoId: String) {
        viewModelScope.launch {
            try {
                goalTodoRepository.deleteGoalTodo(uid, goalTodoId)

                _state.value = _state.value.copy(
                    todos = _state.value.todos.filterNot { it.goalTodoId == goalTodoId }
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    fun addGoalTodo(
        uid: String,
        goalId: String,
        title: String,
        dueDate: String?,
        onResult: (Boolean) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val newGoalTodo = GoalTodo(
                    goalTodoId = "",
                    goalId = goalId,
                    uid = uid,
                    title = title,
                    dueDate = dueDate,
                    completed = false,
                    createdAt = Timestamp.now(),
                )
                val savedTodo = goalTodoRepository.createGoalTodo(uid, newGoalTodo)

                _state.value = _state.value.copy(
                    todos = _state.value.todos + savedTodo
                )
                onResult(true)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
                onResult(false)
            }
        }
    }

    fun deleteGoalWithTodos(uid: String, goalId: String) {
        viewModelScope.launch {
            try {
                val todos = goalTodoRepository.getGoalTodosByGoalId(uid, goalId)
                todos.forEach { todo ->
                    goalTodoRepository.deleteGoalTodo(uid, todo.goalTodoId)

                    val selectedGoals = goalRepository.getGoals(uid)
                    if(selectedGoals.isNotEmpty()){
                        updateDayCountWidget()
                    }
                }
                goalRepository.deleteGoal(uid, goalId)
                _state.value = _state.value.copy(goalDeleted = true)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message, goalDeleted = false)
            }
        }
    }

    fun resetGoalDeleted() {
        _state.value = _state.value.copy(goalDeleted = false)
    }

    fun updateDayCountWidget(){
        val intent = Intent(context, DayCountWidgetReceiver::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        }
        context.sendBroadcast(intent)
    }
}
