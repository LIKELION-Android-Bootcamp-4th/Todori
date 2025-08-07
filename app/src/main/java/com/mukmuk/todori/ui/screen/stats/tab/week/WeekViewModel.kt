package com.mukmuk.todori.ui.screen.stats.tab.week

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mukmuk.todori.data.remote.todo.Todo
import com.mukmuk.todori.data.repository.TodoRepository
import com.mukmuk.todori.ui.screen.todo.detail.todo.TodoDetailState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
@RequiresApi(Build.VERSION_CODES.O)
class WeekViewModel @Inject constructor(
    private val todoRepository: TodoRepository
): ViewModel() {
    private val _todos = MutableStateFlow(emptyList<Todo>())
    val todos: StateFlow<List<Todo>> = _todos

    private val _completedTodos = MutableStateFlow(emptyList<Todo>())
    val completedTodos: StateFlow<List<Todo>> = _completedTodos


    //선택 날짜에 해당하는 주 가져오기
    fun getWeekRange(date: LocalDate): List<LocalDate> {
        val dayOfWeek = date.dayOfWeek.value % 7
        val sunday = date.minusDays(dayOfWeek.toLong())
        return (0..6).map { sunday.plusDays(it.toLong()) }
    }

    fun loadWeekTodos(uid: String, date: LocalDate) {
        viewModelScope.launch {
            try {
                val weekRange = getWeekRange(date)
                val allTodos = todoRepository.getTodosByDate(uid, date)
                val weeklyTodos = allTodos.filter { todo ->
                    val Date = LocalDate.parse(todo.date)
                    //date말구??
                    Date in weekRange
                }
                _todos.value = weeklyTodos

            } catch (e: Exception){
                Log.d("WeekViewModel", "주간 투두 불러오기 실패 : ${e.message}")
            }
        }
    }

    fun loadWeekCompletedTodos(uid: String, date: LocalDate) {
        viewModelScope.launch {
            try {
                val weekRange = getWeekRange(date)
                val allTodos = todoRepository.getTodosByDate(uid, date)
                val weeklyTodos = allTodos.filter { todo ->
                    val Date = LocalDate.parse(todo.date)
                    Date in weekRange
                }
                _completedTodos.value = weeklyTodos.filter { it.completed }

            } catch (e: Exception){
                Log.d("WeekViewModel", "주간 완료 투두 불러오기 실패 : ${e.message}")
            }
        }
    }
}