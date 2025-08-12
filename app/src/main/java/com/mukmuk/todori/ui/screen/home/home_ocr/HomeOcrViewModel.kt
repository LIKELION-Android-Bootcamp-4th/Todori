package com.mukmuk.todori.ui.screen.home.home_ocr

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognizer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject


@HiltViewModel
class HomeOcrViewModel @Inject constructor(
    private val textRecognizer: TextRecognizer
) : ViewModel() {

    private val _state = MutableStateFlow(HomeOcrState())
    val state: StateFlow<HomeOcrState> = _state.asStateFlow()

    private val timePattern1 = Pattern.compile("(\\d{1,2}):(\\d{2}):(\\d{2})")
    private val timePattern2 = Pattern.compile("(\\d{1,2}):(\\d{2})")

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

    fun processGalleryImage(uri: Uri?, context: android.content.Context) {
        if (uri == null) {
            setOcrMode(OcrMode.CAMERA_PREVIEW)
            return
        }

        _state.update {
            it.copy(
                selectedImageUri = uri,
                ocrMode = OcrMode.GALLERY_IMAGE,
                recognizedText = "인식 중...",
                isOcrProcessing = true
            )
        }

        viewModelScope.launch {
            val bitmap = try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(
                        ImageDecoder.createSource(
                            context.contentResolver,
                            uri
                        )
                    )
                } else {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }
            } catch (e: Exception) {
                Log.e("HomeOcrViewModel", "갤러리 이미지 로드 실패: ${e.message}", e)
                _state.update {
                    it.copy(
                        recognizedText = "이미지 로드 실패: ${e.message}",
                        isOcrProcessing = false
                    )
                }
                return@launch
            }

            bitmap?.let { btm ->
                val image = InputImage.fromBitmap(btm, 0)
                textRecognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        val fullText = visionText.text
                        val parsedTime = parseTimeFromText(fullText)    // 시간으로 변환

                        _state.update {
                            it.copy(
                                recognizedText = fullText,  // 일단 원본 인식 텍스트 설정
                                isOcrProcessing = false
                            )
                        }
                    }
                    .addOnFailureListener { e ->
                        _state.update {
                            it.copy(
                                recognizedText = "OCR 실패: ${e.message}",
                                isOcrProcessing = false
                            )
                        }
                        Log.e("HomeOcrViewModel", "OCR 실패", e)
                    }
            } ?: run {
                _state.update {
                    it.copy(
                        recognizedText = "이미지를 찾을 수 없습니다.",
                        isOcrProcessing = false
                    )
                }
            }
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