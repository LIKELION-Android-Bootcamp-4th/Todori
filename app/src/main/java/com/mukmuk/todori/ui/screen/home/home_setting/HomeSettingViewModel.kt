package com.mukmuk.todori.ui.screen.home.home_setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mukmuk.todori.data.local.datastore.HomeSettingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeSettingViewModel @Inject constructor(
    private val repository: HomeSettingRepository
) : ViewModel() {
    private val _state = MutableStateFlow(HomeSettingState())
    val state: StateFlow<HomeSettingState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.value = repository.homeSettingStateFlow.first()
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