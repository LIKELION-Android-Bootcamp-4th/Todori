package com.mukmuk.todori.ui.screen.home.home_setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mukmuk.todori.data.local.datastore.HomeSettingRepository
import com.mukmuk.todori.ui.screen.home.PomodoroTimerMode
import com.mukmuk.todori.ui.screen.home.TimerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel // Hilt를 통해 ViewModel을 주입받겠다는 어노테이션
class HomeSettingViewModel @Inject constructor( // 생성자에 @Inject 어노테이션
    private val repository: HomeSettingRepository // Hilt가 이 인스턴스를 제공
)  : ViewModel() {
    private val _state = MutableStateFlow(HomeSettingState())
    val state: StateFlow<HomeSettingState> = _state

    init {
        // ViewModel 초기화 시 저장된 설정 불러오기
        viewModelScope.launch {
            repository.homeSettingStateFlow.collect { loadedState ->
                _state.value = loadedState
            }
        }
    }

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
        saveSettings()
    }

    private fun saveSettings() {
        viewModelScope.launch {
            repository.saveHomeSettingState(_state.value)
        }
    }
}