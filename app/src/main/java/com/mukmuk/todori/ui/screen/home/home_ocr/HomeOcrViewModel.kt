package com.mukmuk.todori.ui.screen.home.home_ocr

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mukmuk.todori.data.local.datastore.RecordSettingRepository
import com.mukmuk.todori.data.remote.dailyRecord.DailyRecord
import com.mukmuk.todori.data.repository.HomeRepository
import com.mukmuk.todori.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.regex.Pattern
import javax.inject.Inject


@HiltViewModel
class HomeOcrViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val repository: UserRepository,
    private val recordSettingRepository: RecordSettingRepository
    ) : ViewModel() {

    private val _state = MutableStateFlow(HomeOcrState())
    val state: StateFlow<HomeOcrState> = _state.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    private val currentDate = LocalDate.now()

    fun loadProfile(uid: String) {
        viewModelScope.launch {
            runCatching { repository.getProfile(uid) }
                .onSuccess {
                    _state.value = _state.value.copy(uid = uid)
                }
                .onFailure { e ->
                    Log.e("todorilog", "${e.message}")
                }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onAddRecordTime() {
        viewModelScope.launch {
            val hours = _state.value.selfInputHours
            val minutes = _state.value.selfInputMinutes
            val seconds = _state.value.selfInputSeconds

            val existingTotalTimeInMillis =
                homeRepository.getDailyRecord(uid = _state.value.uid, date = currentDate)
                    .firstOrNull()?.studyTimeMillis ?: 0L
            val newTotalTimeInMillis = (hours * 3600L + minutes * 60L + seconds) * 1000L

            recordSettingRepository.saveTotalRecordTime(existingTotalTimeInMillis + newTotalTimeInMillis)

            if (newTotalTimeInMillis > 0) {
                val newDailyRecord = DailyRecord(
                    date = currentDate.toString(),
                    studyTimeMillis = existingTotalTimeInMillis + newTotalTimeInMillis
                )
                try {
                    homeRepository.updateDailyRecord(_state.value.uid, newDailyRecord)
                } catch (e: Exception) {
                    Log.e("todorilog", "Firebase 업데이트 실패: ${e.message}")
                }
            } else {
                Log.e("todorilog", "추가할 시간이 0보다 작거나 같습니다.")
            }
        }
    }

    fun setTimeState(hours: Int, minutes: Int, seconds: Int) {
        _state.update {
            it.copy(
                selfInputHours = hours,
                selfInputMinutes = minutes,
                selfInputSeconds = seconds
            )
        }
    }

    fun onSelfInputChanged(hours: Int, minutes: Int, seconds: Int) {
        _state.update {
            it.copy(
                selfInputHours = hours,
                selfInputMinutes = minutes,
                selfInputSeconds = seconds
            )
        }
    }
}