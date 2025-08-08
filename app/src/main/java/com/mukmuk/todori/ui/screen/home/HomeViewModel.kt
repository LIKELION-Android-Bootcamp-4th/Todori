package com.mukmuk.todori.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mukmuk.todori.data.local.datastore.HomeSettingRepository
import com.mukmuk.todori.ui.screen.home.home_setting.HomeSettingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest // collectLatest import
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeSettingRepository: HomeSettingRepository // HomeSettingRepository 주입
) : ViewModel() {

    private val _state = MutableStateFlow(TimerState())
    val state: StateFlow<TimerState> = _state

    private var timerJob: Job? = null
    private var currentSettings: HomeSettingState = HomeSettingState()

    init {
        viewModelScope.launch {
            homeSettingRepository.homeSettingStateFlow.collectLatest { settings ->
                currentSettings = settings
                updateInitialTimerSettings(settings)
            }
        }
    }

    fun onEvent(event: TimerEvent) {
        when (event) {
            is TimerEvent.Start -> startTimer()
            is TimerEvent.Resume -> resumeTimer()
            is TimerEvent.Stop -> stopTimer()
            is TimerEvent.Reset -> resetTimer()
            is TimerEvent.Record -> enterRecordMode()
        }
    }

    private fun startTimer() {
        if (_state.value.status == TimerStatus.RUNNING) return

        _state.update { it.copy(status = TimerStatus.RUNNING) }

        timerJob = viewModelScope.launch {
            while (_state.value.timeLeftInMillis > 0) {
                delay(1000)
                if (_state.value.pomodoroMode == PomodoroTimerMode.FOCUSED) {
                    _state.update {
                        it.copy(
                            timeLeftInMillis = it.timeLeftInMillis - 1000,
                            totalStudyTimeMills = it.totalStudyTimeMills + 1000
                        )
                    }
                } else {
                    _state.update {
                        it.copy(
                            timeLeftInMillis = it.timeLeftInMillis - 1000,
                        )
                    }
                }
            }
            handleTimerCompletion()
        }
    }

    private fun resumeTimer() {
        startTimer()
    }

    private fun stopTimer() {
        timerJob?.cancel()
        _state.update { it.copy(status = TimerStatus.IDLE) }
    }

    private fun resetTimer() {
        timerJob?.cancel()
        _state.update {
            it.copy(
                timeLeftInMillis = (currentSettings.focusMinutes * 60 + currentSettings.focusSeconds) * 1000L,
                completedFocusCycles = 0,
                status = TimerStatus.IDLE,
                pomodoroMode = PomodoroTimerMode.FOCUSED
            )
        }
    }

    private fun enterRecordMode() {
        timerJob?.cancel()
        _state.update { it.copy(status = TimerStatus.RECORDING) }
    }

    fun setTotalRecordTimeMills(recordTime: Long) {
        _state.update { it.copy(totalRecordTimeMills = _state.value.totalRecordTimeMills + recordTime) }
    }

    private fun updateInitialTimerSettings(settings: HomeSettingState) {
        _state.update {
            it.copy(
                timeLeftInMillis = (settings.focusMinutes * 60 + settings.focusSeconds) * 1000L,
                pomodoroMode = PomodoroTimerMode.FOCUSED,
                completedFocusCycles = 0,
                isPomodoroEnabled = settings.isPomodoroEnabled,
                status = TimerStatus.IDLE
            )
        }
    }

    private fun handleTimerCompletion() {
        timerJob?.cancel()

        if (currentSettings.isPomodoroEnabled) {
            when (_state.value.pomodoroMode) {
                PomodoroTimerMode.FOCUSED -> {
                    val nextCompletedCycles = _state.value.completedFocusCycles + 1

                    if (nextCompletedCycles % 4 == 0) {
                        _state.update { currentState ->
                            currentState.copy(
                                pomodoroMode = PomodoroTimerMode.LONG_RESTED,
                                timeLeftInMillis = (currentSettings.longRestMinutes * 60 + currentSettings.longRestSeconds) * 1000L,
                                completedFocusCycles = nextCompletedCycles,
                                status = TimerStatus.IDLE
                            )
                        }
                    } else {
                        _state.update { currentState ->
                            currentState.copy(
                                pomodoroMode = PomodoroTimerMode.SHORT_RESTED,
                                timeLeftInMillis = (currentSettings.shortRestMinutes * 60 + currentSettings.shortRestSeconds) * 1000L,
                                completedFocusCycles = nextCompletedCycles,
                                status = TimerStatus.IDLE
                            )
                        }
                    }
                }

                PomodoroTimerMode.SHORT_RESTED -> {
                    _state.update { currentState ->
                        currentState.copy(
                            pomodoroMode = PomodoroTimerMode.FOCUSED,
                            timeLeftInMillis = (currentSettings.focusMinutes * 60 + currentSettings.focusSeconds) * 1000L,
                            status = TimerStatus.IDLE
                        )
                    }
                }

                PomodoroTimerMode.LONG_RESTED -> {
                    _state.update { currentState ->
                        currentState.copy(
                            pomodoroMode = PomodoroTimerMode.FOCUSED,
                            timeLeftInMillis = (currentSettings.focusMinutes * 60 + currentSettings.focusSeconds) * 1000L,
                            completedFocusCycles = 0,
                            status = TimerStatus.IDLE
                        )
                    }
                }
            }
        } else {
            _state.update { currentState ->
                currentState.copy(
                    pomodoroMode = PomodoroTimerMode.FOCUSED,
                    timeLeftInMillis = (currentSettings.focusMinutes * 60 + currentSettings.focusSeconds) * 1000L,
                    completedFocusCycles = 0, // 사이클 초기화
                    status = TimerStatus.IDLE // 완료 후 IDLE 상태로
                )
            }
        }
    }
}