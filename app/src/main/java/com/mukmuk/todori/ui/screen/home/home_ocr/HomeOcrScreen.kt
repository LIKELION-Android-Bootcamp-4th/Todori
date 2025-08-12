package com.mukmuk.todori.ui.screen.home.home_ocr

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.mukmuk.todori.ui.theme.Dimens
import java.util.concurrent.Executors

@Composable
fun HomeOcrScreen(
    viewModel: HomeOcrViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val state by viewModel.state.collectAsState()

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        viewModel.updateCameraPermissionStatus(isGranted)
    }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.processGalleryImage(uri, context)
    }

    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val textRecognizer = remember { TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS) }
    val analysisExecutor = remember { Executors.newSingleThreadExecutor() }

    LaunchedEffect(Unit) {
        val isGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        viewModel.updateCameraPermissionStatus(isGranted)
        if (!isGranted) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (state.hasCameraPermission) {
            if (state.ocrMode == OcrMode.CAMERA_PREVIEW) {
                AndroidView(
                    factory = { ctx ->
                        PreviewView(ctx).apply {
                            this.scaleType = PreviewView.ScaleType.FILL_CENTER
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.Medium)
                        .height(300.dp),
                ) { previewView ->
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                        val imageAnalysis = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                            .also {
                                // SimpleTextAnalyzer의 콜백이 ViewModel의 메서드를 호출
                                it.setAnalyzer(analysisExecutor, SimpleTextAnalyzer(textRecognizer) { numbers ->
                                    viewModel.onRealtimeOcrResult(numbers)
                                })
                            }

                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                CameraSelector.DEFAULT_BACK_CAMERA,
                                preview,
                                imageAnalysis
                            )
                        } catch (exc: Exception) {
                            Log.e("HomeOcrScreen", "유스케이스 바인딩 실패", exc)
                        }
                    }, ContextCompat.getMainExecutor(context))
                }
            } else {
                state.selectedImageUri?.let { uri ->
                    val bitmap = remember(uri) {
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
                            } else {
                                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                            }
                        } catch (e: Exception) {
                            Log.e("HomeOcrScreen", "이미지 미리보기 로드 실패", e)
                            null
                        }
                    }
                    bitmap?.let { btm ->
                        Image(
                            bitmap = btm.asImageBitmap(),
                            contentDescription = "Selected Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .padding(horizontal = Dimens.Medium)
                        )
                    } ?: Text("이미지 로드 오류")
                }
                if (state.isOcrProcessing) {
                    CircularProgressIndicator()
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 모드 전환 버튼
            Button(
                onClick = {
                    viewModel.setOcrMode(OcrMode.GALLERY_IMAGE)
                    pickImageLauncher.launch("image/*")
                },
                modifier = Modifier.fillMaxWidth().padding(horizontal = Dimens.Medium)
            ) {
                Text("갤러리에서 이미지 선택 🖼️")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    viewModel.setOcrMode(OcrMode.CAMERA_PREVIEW)
                },
                modifier = Modifier.fillMaxWidth().padding(horizontal = Dimens.Medium)
            ) {
                Text("실시간 카메라 OCR 모드 📸")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    viewModel.setOcrMode(OcrMode.SELF)
                },
                modifier = Modifier.fillMaxWidth().padding(horizontal = Dimens.Medium)
            ) {
                Text("직접 입력 모드 📝")
            }


            Spacer(modifier = Modifier.height(16.dp))

            Text(text = state.recognizedText, modifier = Modifier.padding(16.dp))
        } else {
            Spacer(modifier = Modifier.height(32.dp))
            Text("카메라 권한이 필요합니다.", modifier = Modifier.padding(16.dp))
            Button(onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) }) {
                Text("권한 요청")
            }
        }
    }
}

class SimpleTextAnalyzer(
    private val textRecognizer: TextRecognizer,
    private val onNumbersRecognized: (String) -> Unit
) : ImageAnalysis.Analyzer {

    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            textRecognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val numbers = visionText.textBlocks.flatMap { block ->
                        block.lines.flatMap { line ->
                            line.elements.mapNotNull { element ->
                                element.text.filter { it.isDigit() }
                            }
                        }
                    }.joinToString(" ")
                    onNumbersRecognized(numbers)
                }
                .addOnFailureListener { e ->
                    Log.e("SimpleTextAnalyzer", "텍스트 인식 실패", e)
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }
}