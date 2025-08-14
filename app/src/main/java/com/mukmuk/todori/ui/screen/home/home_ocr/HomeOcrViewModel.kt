package com.mukmuk.todori.ui.screen.home.home_ocr

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.text.TextRecognizer
import com.mukmuk.todori.data.local.datastore.RecordRepository
import com.mukmuk.todori.data.remote.dailyRecord.DailyRecord
import com.mukmuk.todori.data.remote.todo.Todo
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
    private val textRecognizer: TextRecognizer
) : ViewModel() {

    private val _state = MutableStateFlow(HomeOcrState())
    val state: StateFlow<HomeOcrState> = _state.asStateFlow()

    private val timePattern1 = Pattern.compile("(\\d{1,2}):(\\d{2}):(\\d{2})")
    private val timePattern2 = Pattern.compile("(\\d{1,2}):(\\d{2})")

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

    fun updateCameraPermissionStatus(isGranted: Boolean) {
        _state.update { it.copy(hasCameraPermission = isGranted) }
    }

    fun setOcrMode(mode: OcrMode) {
        _state.update {
            it.copy(
                ocrMode = mode,
                recognizedText = "인식된 숫자: N/A",
                selectedImageUri = null
            )
        }
    }

    fun onRealtimeOcrResult(fullText: String) {
        if (_state.value.ocrMode == OcrMode.CAMERA_PREVIEW) {
            val parsedTime = parseTimeFromText(fullText)
            _state.update { it.copy(recognizedText = parsedTime) }
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

    private fun parseTimeFromText(fullText: String): String {
        var extractedTime: String = "시간 인식 실패"

        val matcher1 = timePattern1.matcher(fullText)
        if (matcher1.find()) {
            val hours = matcher1.group(1)
            val minutes = matcher1.group(2)
            val seconds = matcher1.group(3)
            if (minutes?.toIntOrNull() in 0..59 && seconds?.toIntOrNull() in 0..59) {
                return "인식된 시간: ${hours}시 ${minutes}분 ${seconds}초"
            }
        }

        val matcher2 = timePattern2.matcher(fullText)
        if (matcher2.find()) {
            val part1 = matcher2.group(1)
            val part2 = matcher2.group(2)
            if (part1?.toIntOrNull() in 0..59 && part2?.toIntOrNull() in 0..59) {
                return "인식된 시간: ${part1}분 ${part2}초"
            }
        }

        return extractedTime
    }
}