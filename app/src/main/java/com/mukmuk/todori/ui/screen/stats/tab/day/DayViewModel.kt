package com.mukmuk.todori.ui.screen.stats.tab.day

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mukmuk.todori.data.remote.dailyRecord.DailyRecord
import com.mukmuk.todori.data.remote.dailyRecord.ReflectionV2
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
@RequiresApi(Build.VERSION_CODES.O)
class DayViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val todoRepository: TodoRepository,
    private val dailyRecordRepository: DailyRecordRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DayState())
    val state: StateFlow<DayState> = _state.asStateFlow()

    private var loadedMonth: YearMonth? = null

    init {
        refreshMonth()
        refreshSelected()
    }


    fun onDateSelected(date: LocalDate) {
        if (date == _state.value.selectedDate) return

        updateState { copy(selectedDate = date) }
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

    fun updateReflection(newReflection: String) {
        val uid = uidOrNull() ?: return
        val date = _state.value.selectedDate

        updateState { copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val record = dailyRecordRepository.getRecordByDate(uid, date)
                    ?: DailyRecord(date = date.toString(), uid = uid)
                val updatedRecord = record.copy(reflection = newReflection)

                dailyRecordRepository.updateDailyRecord(uid, updatedRecord)
                refreshSelected()
            } catch (e: Exception) {
                Log.e("DayViewModel", "Reflection 업데이트 실패", e)
                updateState {
                    copy(
                        isLoading = false,
                        error = "회고 저장에 실패했습니다: ${e.message}"
                    )
                }
            }
        }
    }

    fun updateReflectionV2(reflectionV2: ReflectionV2) {
        val uid = uidOrNull() ?: return
        val date = _state.value.selectedDate

        updateState { copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                dailyRecordRepository.updateReflectionV2(uid, date, reflectionV2)
                refreshSelected()
            } catch (e: Exception) {
                Log.e("DayViewModel", "ReflectionV2 업데이트 실패", e)
                updateState {
                    copy(
                        isLoading = false,
                        error = "회고 저장에 실패했습니다: ${e.message}"
                    )
                }
            }
        }
    }

    fun updateDailyRecord(record: DailyRecord) {
        val uid = uidOrNull() ?: return

        updateState { copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                dailyRecordRepository.updateDailyRecord(uid, record)
                refreshSelected()
            } catch (e: Exception) {
                Log.e("DayViewModel", "DailyRecord 업데이트 실패", e)
                updateState {
                    copy(
                        isLoading = false,
                        error = "기록 저장에 실패했습니다: ${e.message}"
                    )
                }
            }
        }
    }

    fun clearError() {
        updateState { copy(error = null) }
    }

    private fun uidOrNull(): String? = auth.currentUser?.uid ?: "testuser"

    private fun updateState(update: DayState.() -> DayState) {
        _state.value = _state.value.update()
    }

    private fun refreshMonth(
        year: Int = LocalDate.now().year,
        month: Int = LocalDate.now().monthValue
    ) = viewModelScope.launch {
        val uid = uidOrNull() ?: return@launch

        updateState { copy(isLoading = true) }

        try {
            val records = dailyRecordRepository.getRecordsByMonth(uid, year, month)
            updateState {
                copy(
                    monthRecords = records,
                    isLoading = false,
                    error = null
                )
            }
        } catch (e: Exception) {
            Log.e("DayViewModel", "월간 기록 로딩 실패", e)
            updateState {
                copy(
                    isLoading = false,
                    error = "데이터 로딩에 실패했습니다: ${e.message}"
                )
            }
        }
    }

    private fun refreshSelected() = viewModelScope.launch {
        val uid = uidOrNull() ?: return@launch
        val date = _state.value.selectedDate

        updateState { copy(isLoading = true) }

        try {
            // 투두와 기록을 동시에 가져오기
            val todos = runCatching {
                todoRepository.getTodosByDate(uid, date)
            }.getOrElse { emptyList() }

            val record = runCatching {
                dailyRecordRepository.getRecordByDate(uid, date)
            }.getOrNull()

            updateState {
                copy(
                    todos = todos,
                    selectedRecord = record,
                    isLoading = false,
                    error = null
                )
            }
        } catch (e: Exception) {
            Log.e("DayViewModel", "선택된 날짜 데이터 로딩 실패", e)
            updateState {
                copy(
                    isLoading = false,
                    error = "데이터 로딩에 실패했습니다: ${e.message}"
                )
            }
        }
    }
}
