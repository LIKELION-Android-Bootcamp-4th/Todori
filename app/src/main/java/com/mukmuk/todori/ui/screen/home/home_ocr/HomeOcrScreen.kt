package com.mukmuk.todori.ui.screen.home.home_ocr

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.mukmuk.todori.ui.screen.home.components.TimerTextFieldInput2
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Background
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.DarkGray
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Gray
import com.mukmuk.todori.ui.theme.UserPrimary
import com.mukmuk.todori.ui.theme.White
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeOcrScreen(
    navController: NavHostController,
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

    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val textRecognizer =
        remember { TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS) }
    val analysisExecutor = remember { Executors.newSingleThreadExecutor() }

    LaunchedEffect(Unit) {
        val isGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        viewModel.updateCameraPermissionStatus(isGranted)
        if (!isGranted) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "시간 추가",
                        style = AppTextStyle.AppBar
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(Background),
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigateUp()
                    }) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = "Back Button",
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Dimens.Medium),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.Medium)
                        .clip(RoundedCornerShape(Dimens.Medium))
                        .background(Gray)
                        .border(1.dp, Gray, RoundedCornerShape(Dimens.Small)),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { viewModel.setOcrMode(OcrMode.SELF) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (state.ocrMode == OcrMode.SELF) White else Color.Transparent,
                            contentColor = if (state.ocrMode == OcrMode.SELF) Black else Black
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(Dimens.XXLarge)
                    ) {
                        Text("직접 입력")
                    }
                    Button(
                        onClick = { viewModel.setOcrMode(OcrMode.CAMERA_PREVIEW) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (state.ocrMode == OcrMode.CAMERA_PREVIEW) White else Color.Transparent,
                            contentColor = if (state.ocrMode == OcrMode.CAMERA_PREVIEW) Black else Black
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(Dimens.XXLarge)
                    ) {
                        Text("카메라 OCR")
                    }
                }
                Spacer(modifier = Modifier.height(Dimens.Medium))
                Text(
                    "추가된 시간은 되돌릴 수 없습니다.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }

            if (state.ocrMode == OcrMode.SELF) {
                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ){
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Dimens.Medium),
                        contentAlignment = Alignment.Center
                    ) {
                        ElevatedCard(
                            colors = CardDefaults.cardColors(
                                containerColor = White
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 10.dp
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(Dimens.Medium),
                            ) {
                                Text(
                                    text = "빠른 선택",
                                    style = AppTextStyle.BodySmall,
                                    color = DarkGray
                                )
                                Spacer(modifier = Modifier.height(Dimens.Medium))
                                Row {
                                    OutlinedButton(
                                        onClick = {
                                            navController.navigateUp()
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f),
                                        border = BorderStroke(1.dp, Gray)
                                    ) {
                                        Text("30분", color = Black)
                                    }
                                    Spacer(modifier = Modifier.width(Dimens.Small))
                                    OutlinedButton(
                                        onClick = {
                                            navController.navigateUp()
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f),
                                        border = BorderStroke(1.dp, Gray)
                                    ) {
                                        Text("1시간", color = Black)
                                    }
                                }
                                Row {
                                    OutlinedButton (
                                        onClick = {
                                            navController.navigateUp()
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f),
                                        border = BorderStroke(1.dp, Gray)
                                    ) {
                                        Text("1시간 30분", color = Black)
                                    }
                                    Spacer(modifier = Modifier.width(Dimens.Small))
                                    OutlinedButton(
                                        onClick = {
                                            navController.navigateUp()
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f),
                                        border = BorderStroke(1.dp, Gray)
                                    ) {
                                        Text("2시간", color = Black)
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(Dimens.Medium))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = Dimens.Medium),
                    ) {
                        ElevatedCard(
                            colors = CardDefaults.cardColors(
                                containerColor = White
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 10.dp
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(Dimens.Medium),
                            ) {
                                Text(
                                    text = "시간 직접 입력",
                                    style = AppTextStyle.BodySmall,
                                    color = DarkGray
                                )
                                Spacer(modifier = Modifier.height(Dimens.Medium))
                                TimerTextFieldInput2(
                                    initialHours = state.selfInputHours,
                                    initialMinutes = state.selfInputMinutes,
                                    initialSeconds = state.selfInputSeconds,
                                    onTimeChanged = { hours, minutes, seconds ->
                                        viewModel.onSelfInputChanged(hours, minutes, seconds)
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = Dimens.Medium),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(450.dp)
                    ) {
                        AndroidView(
                            factory = { ctx ->
                                PreviewView(ctx).apply {
                                    this.scaleType = PreviewView.ScaleType.FIT_CENTER
                                }
                            },
                            modifier = Modifier.fillMaxSize(),
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
                                        it.setAnalyzer(
                                            analysisExecutor,
                                            SimpleTextAnalyzer(textRecognizer) { fullText ->
                                                viewModel.onRealtimeOcrResult(fullText)
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
                    }
                    Spacer(modifier = Modifier.height(Dimens.Medium))
                    Text(
                        text = state.recognizedText,
                        modifier = Modifier.padding(horizontal = Dimens.Medium),
                        style = AppTextStyle.Body
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.Medium),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!state.hasCameraPermission) {
                    Spacer(modifier = Modifier.height(Dimens.Medium))
                    Text("카메라 권한이 필요합니다.", modifier = Modifier.padding(horizontal = Dimens.Medium))
                    Button(onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) }) {
                        Text("권한 요청")
                    }
                }
            }

            Button(
                onClick = {
                    navController.navigateUp()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.Medium)
            ) {
                Text("추가하기")
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
                    val fullText = visionText.text
                    onNumbersRecognized(fullText)
                    imageProxy.close()
                }
                .addOnFailureListener { e ->
                    Log.e("SimpleTextAnalyzer", "텍스트 인식 실패", e)
                    onNumbersRecognized("OCR 실패")
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }
}