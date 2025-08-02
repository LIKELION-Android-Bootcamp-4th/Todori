package com.mukmuk.todori.ui.screen.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mukmuk.todori.ui.screen.home.home_setting.HomeSettingState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val _state = MutableStateFlow(TimerState())
    val state: StateFlow<TimerState> = _state

    private var timerJob: Job? = null
    private var currentSettings: HomeSettingState = HomeSettingState()

    // 초기값 반영
    init {
        val initialSettings = savedStateHandle.get<HomeSettingState>("homeSetting")
            ?: HomeSettingState()

        currentSettings = initialSettings

        _state.value = _state.value.copy(
            timeLeftInMillis = (initialSettings.focusMinutes * 60 + initialSettings.focusSeconds) * 1000L,
            pomodoroMode = PomodoroTimerMode.FOCUSED,
            completedFocusCycles = 0,
            isPomodoroEnabled = initialSettings.isPomodoroEnabled
        )
    }

    fun onEvent(event: TimerEvent) {
        when (event) {
            is TimerEvent.Start -> startTimer()
            is TimerEvent.Resume -> resumeTimer()
            is TimerEvent.Stop -> stopTimer()
            is TimerEvent.Reset -> resetTimer()
        }
    }

    private fun startTimer() {
        if (_state.value.isRunning) return
        _state.update { it.copy(isRunning = true) } // update 사용

        timerJob = viewModelScope.launch {
            while (_state.value.timeLeftInMillis > 0) {
                delay(1000)
                _state.update { it.copy(timeLeftInMillis = it.timeLeftInMillis - 1000) }
                _state.update { it.copy(totalStudyTimeMills = it.totalStudyTimeMills + 1000) }
            }
            // 타이머가 0이 되었을 때 다음 모드 처리
            handleTimerCompletion()
        }
    }

    private fun resumeTimer() {
        startTimer()
    }

    private fun stopTimer() {
        timerJob?.cancel()
        _state.value = _state.value.copy(isRunning = false)
    }

    private fun resetTimer() {
        timerJob?.cancel()
        _state.value = _state.value.copy(
            timeLeftInMillis = (currentSettings.focusMinutes * 60 + currentSettings.focusSeconds) * 1000L,
            pomodoroMode = PomodoroTimerMode.FOCUSED,
            completedFocusCycles = 0,
            isRunning = false
        )
    }

    // HomeSettingScreen에서 전달받은 설정으로 타이머 상태를 업데이트하는 함수
    fun updateInitialTimerSettings(settings: HomeSettingState) {
        currentSettings = settings // 새 설정 저장

        if (!_state.value.isRunning) {
            _state.value = _state.value.copy(
                timeLeftInMillis = (settings.focusMinutes * 60 + settings.focusSeconds) * 1000L,
                pomodoroMode = PomodoroTimerMode.FOCUSED,
                completedFocusCycles = 0,
                isPomodoroEnabled = settings.isPomodoroEnabled

            )
        }
    }

    private fun handleTimerCompletion() {
        timerJob?.cancel()
        _state.update {
            it.copy(
                isRunning = false,
            )
        }

        if (currentSettings.isPomodoroEnabled) {
            when (_state.value.pomodoroMode) {
                PomodoroTimerMode.FOCUSED -> {
                    val nextCompletedCycles = _state.value.completedFocusCycles + 1

                    if (nextCompletedCycles % 4 == 0) {
                        _state.update { currentState ->
                            currentState.copy(
                                pomodoroMode = PomodoroTimerMode.LONG_RESTED,
                                timeLeftInMillis = (currentSettings.longRestMinutes * 60 + currentSettings.longRestSeconds) * 1000L,
                                completedFocusCycles = nextCompletedCycles
                            )
                        }
                    } else {
                        _state.update { currentState ->
                            currentState.copy(
                                pomodoroMode = PomodoroTimerMode.SHORT_RESTED,
                                timeLeftInMillis = (currentSettings.shortRestMinutes * 60 + currentSettings.shortRestSeconds) * 1000L,
                                completedFocusCycles = nextCompletedCycles
                            )
                        }
                    }
                }

                PomodoroTimerMode.SHORT_RESTED -> {
                    _state.update { currentState ->
                        currentState.copy(
                            pomodoroMode = PomodoroTimerMode.FOCUSED,
                            timeLeftInMillis = (currentSettings.focusMinutes * 60 + currentSettings.focusSeconds) * 1000L
                        )
                    }
                }

                PomodoroTimerMode.LONG_RESTED -> {
                    _state.update { currentState ->
                        currentState.copy(
                            pomodoroMode = PomodoroTimerMode.FOCUSED,
                            timeLeftInMillis = (currentSettings.focusMinutes * 60 + currentSettings.focusSeconds) * 1000L,
                            completedFocusCycles = 0 // 긴 휴식 후에는 사이클 초기화
                        )
                    }
                }
            }
        } else {
            _state.update { currentState ->
                currentState.copy(
                    pomodoroMode = PomodoroTimerMode.FOCUSED,
                    timeLeftInMillis = (currentSettings.focusMinutes * 60 + currentSettings.focusSeconds) * 1000L,
                    completedFocusCycles = 0 // 사이클 초기화
                )
            }
        }
        startTimer()
    }
}