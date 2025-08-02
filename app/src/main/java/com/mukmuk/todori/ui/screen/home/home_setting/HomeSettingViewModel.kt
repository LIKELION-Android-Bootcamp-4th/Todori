package com.mukmuk.todori.ui.screen.home.home_setting

import androidx.lifecycle.ViewModel
import com.mukmuk.todori.ui.screen.home.PomodoroTimerMode
import com.mukmuk.todori.ui.screen.home.TimerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class HomeSettingViewModel  : ViewModel() {
    private val _state = MutableStateFlow(HomeSettingState())
    val state: StateFlow<HomeSettingState> = _state

    fun onEvent(event: HomeSettingEvent) {
        when (event) {
            is HomeSettingEvent.SetPomodoroEnabled -> {
                _state.value = _state.value.copy(isPomodoroEnabled = event.isEnabled)
            }

            is HomeSettingEvent.SetFocusTime -> {
                _state.value = _state.value.copy(
                    focusMinutes = event.minutes,
                    focusSeconds = event.seconds
                )
            }

            is HomeSettingEvent.SetShortRestTime -> {
                _state.value = _state.value.copy(
                    shortRestMinutes = event.minutes,
                    shortRestSeconds = event.seconds
                )
            }

            is HomeSettingEvent.SetLongRestTime -> {
                _state.value = _state.value.copy(
                    longRestMinutes = event.minutes,
                    longRestSeconds = event.seconds
                )
            }
        }
    }
}