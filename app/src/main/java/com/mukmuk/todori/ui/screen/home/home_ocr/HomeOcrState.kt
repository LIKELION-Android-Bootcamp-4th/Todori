package com.mukmuk.todori.ui.screen.home.home_ocr

import android.net.Uri

enum class OcrMode {
    CAMERA_PREVIEW,
    SELF
}

data class HomeOcrState(
    val recognizedText: String = "인식된 숫자: N/A",
    val ocrMode: OcrMode = OcrMode.SELF,
    val selectedImageUri: Uri? = null,
    val isOcrProcessing: Boolean = false,
    val hasCameraPermission: Boolean = false,
    val selfInputHours: Int = 0,
    val selfInputMinutes: Int = 0,
    val selfInputSeconds: Int = 0,
    val uid: String = ""
)