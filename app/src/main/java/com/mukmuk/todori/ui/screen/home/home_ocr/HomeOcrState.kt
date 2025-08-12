package com.mukmuk.todori.ui.screen.home.home_ocr

import android.net.Uri

enum class OcrMode {
    CAMERA_PREVIEW,
    GALLERY_IMAGE,
    SELF
}

data class HomeOcrState(
    val recognizedText: String = "인식된 숫자: N/A",
    val ocrMode: OcrMode = OcrMode.CAMERA_PREVIEW,
    val selectedImageUri: Uri? = null,
    val isOcrProcessing: Boolean = false,
    val hasCameraPermission: Boolean = false
)