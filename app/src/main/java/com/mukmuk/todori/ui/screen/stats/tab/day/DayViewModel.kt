package com.mukmuk.todori.ui.screen.stats.tab.day

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mukmuk.todori.data.remote.dailyRecord.DailyRecord
import com.mukmuk.todori.data.remote.todo.Todo
import com.mukmuk.todori.data.repository.DailyRecordRepository
import com.mukmuk.todori.data.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
@RequiresApi(Build.VERSION_CODES.O)
class DayViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val dailyRecordRepository: DailyRecordRepository
) : ViewModel() {
    private val _todos = MutableStateFlow(emptyList<Todo>())
    val todos: StateFlow<List<Todo>> = _todos

    private val _dailyRecord = MutableStateFlow(emptyList<DailyRecord>())
    val dailyRecord: StateFlow<List<DailyRecord>> = _dailyRecord

    private val _completedTodos = MutableStateFlow(emptyList<Todo>())
    val completedTodos: StateFlow<List<Todo>> = _completedTodos

    fun loadTodos(uid: String, date: LocalDate) {
        viewModelScope.launch {
            try {
                val dailyTodos = todoRepository.getTodosByDate(uid, date)
                _todos.value = dailyTodos
                _completedTodos.value = dailyTodos.filter { it.completed }
            } catch (e: Exception) {
                Log.d("DayViewModel", "DAY TODO 불러오기 실패 : ${e.message}")
            }
        }
    }

    fun loadDailyRecord(uid: String, date: LocalDate) {
        viewModelScope.launch {
            try {
                val dailyRecord = dailyRecordRepository.getDailyRecords(uid, date)

                _dailyRecord.value = dailyRecord
            } catch (e: Exception) {
                Log.d("DayViewModel", "DailyRecord 불러오기 실패 : ${e.message}")
            }
        }
    }

    fun updateDailyRecord(uid: String, date: LocalDate, newReflection: String) {
        viewModelScope.launch {
            try {
                val records = dailyRecordRepository.getDailyRecords(uid, date)
                val record = records.firstOrNull() ?: DailyRecord(date = date.toString(), uid = uid)

                val updatedRecord = record.copy(reflection = newReflection)

                dailyRecordRepository.updateDailyRecord(uid, updatedRecord)
            } catch (e: Exception) {
                Log.d("DayViewModel", "DailyRecord 수정 실패 : ${e.message}")
            }
        }
    }
}