package com.mukmuk.todori.ui.screen.home.home_ocr

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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeOcrViewModel @Inject constructor(
    private val textRecognizer: TextRecognizer
): ViewModel() {
    private val _state = MutableStateFlow(HomeOcrState())
    val state: StateFlow<HomeOcrState> = _state

    fun updateCameraPermissionStatus(isGranted: Boolean) {
        _state.update { it.copy(hasCameraPermission = isGranted) }
    }

    fun setOcrMode(mode: OcrMode) {
        _state.update { it.copy(ocrMode = mode, recognizedText = "인식된 숫자: N/A", selectedImageUri = null) }
    }

    fun onRealtimeOcrResult(numbers: String) {
        if (_state.value.ocrMode == OcrMode.CAMERA_PREVIEW) {
            _state.update { it.copy(recognizedText = "인식된 숫자: $numbers") }
        }
    }

    fun processGalleryImage(uri: Uri?, context: android.content.Context) {
        if (uri == null) {
            setOcrMode(OcrMode.CAMERA_PREVIEW)
            return
        }

        _state.update { it.copy(
            selectedImageUri = uri,
            ocrMode = OcrMode.GALLERY_IMAGE,
            recognizedText = "인식 중...",
            isOcrProcessing = true
        ) }

        viewModelScope.launch {
            val bitmap = try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
                } else {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }
            } catch (e: Exception) {
                Log.e("HomeOcrViewModel", "갤러리 이미지 로드 실패: ${e.message}", e)
                _state.update { it.copy(
                    recognizedText = "이미지 로드 실패: ${e.message}",
                    isOcrProcessing = false
                ) }
                return@launch
            }

            bitmap?.let { btm ->
                val image = InputImage.fromBitmap(btm, 0)
                textRecognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        val numbers = visionText.textBlocks.flatMap { block ->
                            block.lines.flatMap { line ->
                                line.elements.mapNotNull { element ->
                                    element.text.filter { it.isDigit() }
                                }
                            }
                        }.joinToString(" ")
                        _state.update { it.copy(
                            recognizedText = "인식된 숫자: $numbers",
                            isOcrProcessing = false
                        ) }
                    }
                    .addOnFailureListener { e ->
                        _state.update { it.copy(
                            recognizedText = "OCR 실패: ${e.message}",
                            isOcrProcessing = false
                        ) }
                        Log.e("HomeOcrViewModel", "OCR 실패", e)
                    }
            } ?: run {
                _state.update { it.copy(
                    recognizedText = "이미지를 찾을 수 없습니다.",
                    isOcrProcessing = false
                ) }
            }
        }
    }
}