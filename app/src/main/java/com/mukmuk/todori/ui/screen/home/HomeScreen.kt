package com.mukmuk.todori.ui.screen.home

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.mukmuk.todori.navigation.BottomNavItem
import com.mukmuk.todori.ui.screen.home.components.MainTodoItemEditableRow
import com.mukmuk.todori.ui.screen.home.components.PomoModeTextBox
import com.mukmuk.todori.ui.screen.home.home_setting.HomeSettingState
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Background
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Gray
import com.mukmuk.todori.ui.theme.UserPrimary
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.util.concurrent.TimeUnit

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController, viewModel: HomeViewModel) {
    val state by viewModel.state.collectAsState()
    val homeSettingState by viewModel.homeSettingState.collectAsState()
    val todoList by viewModel.todoList.collectAsState()

    var selectedIndex by remember { mutableStateOf(-1) }
    var recordTime by remember { mutableStateOf(0L) }
    var recordButtonText by remember { mutableStateOf("기록") }
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle

    val lifecycleOwner = LocalLifecycleOwner.current
    var isUiVisible by remember { mutableStateOf(true) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> isUiVisible = true
                Lifecycle.Event.ON_PAUSE -> isUiVisible = false
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(state.status == TimerStatus.RECORDING) {
        if (state.status != TimerStatus.RECORDING) {
            delay(100L)
            selectedIndex = -1
            recordButtonText = "기록"
        } else {
            recordButtonText = "취소"
        }
    }

    LaunchedEffect(Unit) {
        viewModel.observeAuthAndLoadData()
    }

    LaunchedEffect(savedStateHandle) {
        savedStateHandle?.getLiveData<HomeSettingState>("homeSetting")
            ?.observeForever {
                viewModel.onEvent(TimerEvent.Reset)
            }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                colors = TopAppBarDefaults.topAppBarColors(Background),
                actions = {
                    IconButton(onClick = {
                        navController.navigate("home_setting")
                    }) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = "Timer Setting",
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
            if (isUiVisible) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "누적 공부 시간",
                        style = AppTextStyle.BodyLarge
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = totalFormatTime(state.totalStudyTimeMills),
                        style = AppTextStyle.Timer,
                        textAlign = TextAlign.Center
                    )
                }
                if (homeSettingState.isPomodoroEnabled) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PomoModeTextBox(state.pomodoroMode)
                        Spacer(modifier = Modifier.width(Dimens.Large))
                        Text(
                            text = pomodoroFormatTime(state.timeLeftInMillis),
                            style = AppTextStyle.TitleLarge,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.width(Dimens.Large))
                        IconButton(
                            onClick = { viewModel.onEvent(TimerEvent.Reset) }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Refresh,
                                contentDescription = "Timer Reset"
                            )
                        }

                    }
                }
                Spacer(modifier = Modifier.height(Dimens.XXLarge))
                Row {
                    Button(
                        onClick = {
                            if (state.status == TimerStatus.RUNNING) {
                                viewModel.onEvent(TimerEvent.Record)
                                recordTime = state.totalStudyTimeMills - state.totalRecordTimeMills
                            } else {
                                viewModel.onEvent(TimerEvent.Stop)
                            }
                        },
                        modifier = Modifier.size(76.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Gray,
                            contentColor = Black
                        ),
                        shape = CircleShape
                    ) {
                        Text(text = recordButtonText, style = AppTextStyle.TitleSmall)
                    }
                    Spacer(modifier = Modifier.width(Dimens.XXLarge))
                    if (state.status == TimerStatus.RUNNING) {
                        Button(
                            onClick = { viewModel.onEvent(TimerEvent.Stop) },
                            shape = CircleShape,
                            modifier = Modifier.size(76.dp),
                            contentPadding = PaddingValues(0.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = UserPrimary)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Pause,
                                contentDescription = "Time Start",
                                modifier = Modifier.size(50.dp),
                                tint = Color.White
                            )
                        }
                    } else {
                        Button(
                            onClick = {
                                viewModel.onEvent(TimerEvent.Start)
                                if (state.status == TimerStatus.RECORDING) viewModel.onEvent(TimerEvent.Stop)
                            },
                            shape = CircleShape,
                            modifier = Modifier.size(76.dp),
                            contentPadding = PaddingValues(0.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = UserPrimary)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.PlayArrow,
                                contentDescription = "Time Start",
                                modifier = Modifier.size(50.dp),
                                tint = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(Dimens.XXLarge))

                    Button(
                        onClick = {
                            if (state.status == TimerStatus.RECORDING) viewModel.onEvent(TimerEvent.Stop)
                            navController.navigate("home_ocr")
                        },
                        modifier = Modifier.size(76.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Gray,
                            contentColor = Black
                        ),
                        shape = CircleShape
                    ) {
                        Text(text = "추가", style = AppTextStyle.TitleSmall)
                    }
                }
                Spacer(modifier = Modifier.height(Dimens.XXLarge))
                Spacer(
                    modifier = Modifier
                        .height(1.dp)
                        .fillMaxWidth()
                        .background(Gray)
                )
                Spacer(modifier = Modifier.height(Dimens.XXLarge))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.Medium),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(todoList, key = { it.todoId }) { todo ->
                            MainTodoItemEditableRow(
                                title = todo.title,
                                isDone = todo.completed,
                                isRecordMode = state.status == TimerStatus.RECORDING,
                                recordTime = if (todo.totalFocusTimeMillis > 0L) {
                                    totalFormatTime(todo.totalFocusTimeMillis)
                                } else {
                                    null
                                },
                                onCheckedChange = { checked ->
                                    viewModel.toggleTodoCompleted(uid = state.uid, todo = todo)
                                },
                                onItemClick = {
                                    if (state.status == TimerStatus.RECORDING && !todo.completed) {
                                        viewModel.setTodoRecordTimeMills(
                                            recordTime,
                                            uid = state.uid,
                                            todo = todo
                                        )
                                        viewModel.onEvent(TimerEvent.Stop)
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.height(Dimens.Medium))
                        }
                    }
                }
            }
        }
    }
}


fun pomodoroFormatTime(millis: Long): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
    return String.format("%02d : %02d", minutes, seconds)
}

fun totalFormatTime(millis: Long): String {
    val hours = TimeUnit.MILLISECONDS.toHours((millis))
    val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
    return String.format("%02d : %02d : %02d", hours, minutes, seconds)
}