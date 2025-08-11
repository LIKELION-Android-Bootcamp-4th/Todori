package com.mukmuk.todori.ui.screen.stats.tab.week

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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
@RequiresApi(Build.VERSION_CODES.O)
class WeekViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val dailyRecordRepository: DailyRecordRepository
): ViewModel() {
    private val _state = MutableStateFlow(WeekState())
    val state: StateFlow<WeekState> = _state.asStateFlow()


    //선택 날짜에 해당하는 주 가져오기
    fun getWeekRange(date: LocalDate): List<LocalDate> {
        val dayOfWeek = date.dayOfWeek.value % 7
        val sunday = date.minusDays(dayOfWeek.toLong())
        return (0..6).map { sunday.plusDays(it.toLong()) }
    }

    fun loadWeekTodos(uid: String, date: LocalDate) = viewModelScope.launch {
        try {
            val range = getWeekRange(date)
            val sunday = range.first()
            val saturday = range.last()

            val weeklyTodos = todoRepository.getTodosByWeek(uid, sunday, saturday)
            val completed = weeklyTodos.filter { it.completed }

            _state.update {
                it.copy(
                    todos = weeklyTodos,
                    completedTodoItems = completed,
                    totalTodos = weeklyTodos.size,
                    completedTodos = completed.size
                )
            }
        } catch (e: Exception) {
            Log.d("WeekViewModel", "주간 투두 불러오기 실패 : ${e.message}")
        }
    }

    fun loadWeekStudy(uid: String, date: LocalDate) = viewModelScope.launch {
        try {
            val range = getWeekRange(date)
            val sunday = range.first()
            val saturday = range.last()

            val records = dailyRecordRepository.getRecordsByWeek(uid, sunday, saturday)

            val studied = records.filter { it.studyTimeMillis > 0L }
            val totalSec = studied.sumOf { it.studyTimeMillis }
            val avgSec = if (studied.isNotEmpty()) totalSec / studied.size else 0L

            _state.update {
                it.copy(
                    dailyRecords = records,
                    totalStudyTimeMillis = totalSec * 1000,
                    avgStudyTimeMillis = avgSec * 1000
                )
            }
        } catch (e: Exception) {
            Log.d("WeekViewModel", "주간 공부기록 불러오기 실패 : ${e.message}")
        }
    }
}