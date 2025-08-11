package com.mukmuk.todori.ui.screen.stats.tab.day

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mukmuk.todori.data.remote.dailyRecord.DailyRecord
import com.mukmuk.todori.data.remote.todo.Todo
import com.mukmuk.todori.data.repository.DailyRecordRepository
import com.mukmuk.todori.data.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class DayViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val todoRepository: TodoRepository,
    private val dailyRecordRepository: DailyRecordRepository
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _monthRecords = MutableStateFlow<List<DailyRecord>>(emptyList())
    val monthRecords: StateFlow<List<DailyRecord>> = _monthRecords.asStateFlow()

    private val _todos = MutableStateFlow<List<Todo>>(emptyList())
    val todos: StateFlow<List<Todo>> = _todos.asStateFlow()

    private val _selectedRecord = MutableStateFlow<DailyRecord?>(null)
    val selectedRecord: StateFlow<DailyRecord?> = _selectedRecord.asStateFlow()

    private var loadedMonth: YearMonth? = null

    init {
        refreshMonth()
        refreshSelected()
    }

    fun onDateSelected(date: LocalDate) {
        if (date == _selectedDate.value) return
        _selectedDate.value = date
        refreshSelected()

        val now = LocalDate.now()
        if (date.year != now.year || date.monthValue != now.monthValue) {
            refreshMonth(date.year, date.monthValue)
        }
    }

    fun onMonthChanged(ym: YearMonth) {
        if (ym != loadedMonth) {
            refreshMonth(ym.year, ym.monthValue)
            loadedMonth = ym
        }
    }

    private fun uidOrNull(): String? = auth.currentUser?.uid ?: "testuser"

    private fun refreshMonth(year: Int = LocalDate.now().year, month: Int = LocalDate.now().monthValue) = viewModelScope.launch {
        val uid = uidOrNull() ?: return@launch
        _monthRecords.value = dailyRecordRepository.getRecordsByMonth(uid, year, month)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun refreshSelected() = viewModelScope.launch {
        val uid = uidOrNull() ?: return@launch
        val date = _selectedDate.value

        runCatching { todoRepository.getTodosByDate(uid, date) }
            .onSuccess { _todos.value = it }
            .onFailure { _todos.value = emptyList() }

        runCatching { dailyRecordRepository.getRecordByDate(uid, date) }
            .onSuccess { _selectedRecord.value = it }
            .onFailure { _selectedRecord.value = null }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateDailyRecord(uid: String, date: LocalDate, newReflection: String) {
        viewModelScope.launch {
            try {
                val record = dailyRecordRepository.getRecordByDate(uid, date)
                    ?: DailyRecord(date = date.toString(), uid = uid)

                val updatedRecord = record.copy(reflection = newReflection)

                dailyRecordRepository.updateDailyRecord(uid, updatedRecord)
            } catch (e: Exception) {
                Log.e("DayViewModel", "DailyRecord 수정 실패", e)
            }
        }
    }
}
