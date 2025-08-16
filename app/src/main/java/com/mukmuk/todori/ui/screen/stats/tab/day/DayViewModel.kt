package com.mukmuk.todori.ui.screen.stats.tab.day

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mukmuk.todori.data.remote.dailyRecord.ReflectionV2
import com.mukmuk.todori.data.repository.DailyRecordRepository
import com.mukmuk.todori.data.repository.StudyTargetsRepository
import com.mukmuk.todori.data.repository.TodoRepository
import com.mukmuk.todori.ui.screen.mypage.studytargets.WeeklyPaceData
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
    private val dailyRecordRepository: DailyRecordRepository,
    private val studyTargetsRepository: StudyTargetsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DayState())
    val state: StateFlow<DayState> = _state.asStateFlow()

    private var loadedMonth: YearMonth? = null

    init {
        refreshMonth()  // 이번달 기록
        refreshSelected() //오늘 기록
        observeStudyTargets() //목표치
    }

    /**
     * StudyTargets 변경사항을 Flow로 구독
     * -> Firestore에서 변경되면 state에 자동 반영
     */
    private fun observeStudyTargets() = viewModelScope.launch {
        val uid = uidOrNull() ?: return@launch
        studyTargetsRepository.targetsFlow(uid).collect { targets ->
            updateState { copy(studyTargets = targets) }
        }
    }


    /**
     * 주간 Pace 데이터 계산
     * - 주간 목표치, 현재까지의 누적 공부 시간, 오늘 공부 시간 등을 기반으로
     *   WeeklyPaceData 생성
     */
    fun getWeeklyPaceData(): WeeklyPaceData {
        val targets = _state.value.studyTargets
        val weeklyTargetMinutes = targets?.weeklyMinutes ?: 0
        val selectedDate = _state.value.selectedDate

        // 주 시작일 (월요일 기준)
        val weekStart = selectedDate.minusDays(
            (selectedDate.dayOfWeek.value % 7).toLong()
        )
        // 경과한 일수
        val daysPassed = if (selectedDate.isBefore(LocalDate.now()) || selectedDate == LocalDate.now()) {
            (selectedDate.dayOfWeek.value % 7) + 1
        } else {
            (LocalDate.now().dayOfWeek.value % 7) + 1
        }

        // 실제 주간 누적 공부 시간 (오늘)
        val endDate = if (selectedDate.isAfter(LocalDate.now())) LocalDate.now() else selectedDate
        val actualCumulativeMinutes = calculateWeeklyActualTime(weekStart, endDate)

        // 남은 목표 & 남은 일수
        val remainingMinutes = (weeklyTargetMinutes - actualCumulativeMinutes).coerceAtLeast(0)
        val remainingDays = (7 - daysPassed).coerceAtLeast(1)

        // 앞으로 하루에 필요한 분량
        val requiredDailyMinutes = remainingMinutes / remainingDays

        // 오늘 목표/실제 공부 시간
        val todayTargetMinutes = targets?.dailyMinutes ?: 0
        val todayActualMinutes = _state.value.studyTimeMillis / (1000 * 60)

        return WeeklyPaceData(
            weeklyTargetMinutes = weeklyTargetMinutes,
            requiredDailyMinutes = requiredDailyMinutes.toInt(),
            actualCumulativeMinutes = actualCumulativeMinutes.toInt(),
            todayTargetMinutes = todayTargetMinutes,
            todayActualMinutes = todayActualMinutes.toInt(),
            daysPassed = daysPassed
        )
    }


    /**
     * 특정 주간의 실제 누적 공부 시간 합산
     * - DailyRecord 중에서 해당 주 범위 내 데이터를 가져와 합계
     */
    private fun calculateWeeklyActualTime(weekStart: LocalDate, endDate: LocalDate): Long {
        val weekRecords = _state.value.monthRecords.filter { record ->
            val recordDate = LocalDate.parse(record.date)
            !recordDate.isBefore(weekStart) && !recordDate.isAfter(endDate)
        }

        return weekRecords.sumOf { record ->
            record.studyTimeMillis ?: 0
        } / (1000 * 60) // ms -> min
    }

    /**
     * 날짜 선택 이벤트
     * - 선택된 날짜를 State에 반영
     * - 해당 날짜 데이터 불러오기
     * - 만약 다른 달이라면 새로 월 데이터를 불러옴
     */
    fun onDateSelected(date: LocalDate) {
        if (date == _state.value.selectedDate) return

        updateState { copy(selectedDate = date) }
        refreshSelected()

        val now = LocalDate.now()
        if (date.year != now.year || date.monthValue != now.monthValue) {
            refreshMonth(date.year, date.monthValue)
        }
    }

    /**
     * 달 변경 이벤트
     * - 새로운 YearMonth가 기존과 다를 경우만 새로 로딩
     */
    fun onMonthChanged(ym: YearMonth) {
        if (ym != loadedMonth) {
            refreshMonth(ym.year, ym.monthValue)
            loadedMonth = ym
        }
    }

    /**
     * Reflection 업데이트
     */
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

    /**
     * 에러 상태 초기화
     */
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
