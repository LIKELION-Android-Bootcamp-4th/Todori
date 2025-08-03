package com.mukmuk.todori.ui.screen.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import com.mukmuk.todori.data.remote.todo.Todo
import com.mukmuk.todori.ui.screen.home.components.PomoModeTextBox
import com.mukmuk.todori.ui.screen.home.components.MainTodoItemEditableRow
import com.mukmuk.todori.ui.screen.home.home_setting.HomeSettingState
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Gray
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel, navController: NavHostController) {
    val state by viewModel.state.collectAsState()
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    var selectedIndex by remember { mutableStateOf(-1) }
    var recordTime by remember { mutableStateOf(0L) }
    var recordButtonText by remember { mutableStateOf("기록") }
    val todos = remember {
        mutableStateListOf(
            Todo(title = "스트레칭 하기", isCompleted = true),
            Todo(title = "스쿼트 50개", isCompleted = false),
            Todo(title = "런닝 30분", isCompleted = true),
            Todo(title = "Kotlin 문법 정리", isCompleted = false),
            Todo(title = "Coroutine 복습", isCompleted = true),
            Todo(title = "Jetpack Compose", isCompleted = false),
            Todo(title = "10분 명상", isCompleted = true),
            Todo(title = "감사 일기 작성", isCompleted = false),
            Todo(title = "차분한 음악 듣기", isCompleted = true)
        )
    }


    LaunchedEffect(savedStateHandle) {
        val homeSettingState = savedStateHandle?.get<HomeSettingState>("homeSetting")
        if (homeSettingState != null) {
            viewModel.updateInitialTimerSettings(homeSettingState)
            savedStateHandle.remove<HomeSettingState>("homeSetting")
        }
    }

    LaunchedEffect(state.status == TimerStatus.RECORDING) {
        if (state.status != TimerStatus.RECORDING) {
            delay(300L)
            selectedIndex = -1
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
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
            if (state.isPomodoroEnabled) {
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
                            recordButtonText = "취소"
                        } else {
                            viewModel.onEvent(TimerEvent.Stop)
                            recordButtonText = "기록"
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
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22B282)) // 예시 색상
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Star,
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
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22B282)) // 예시 색상
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
                LazyColumn {
                    itemsIndexed(todos) { index, todo ->
                        MainTodoItemEditableRow(
                            title = todo.title,
                            isDone = todo.isCompleted,
                            isRecordMode = state.status == TimerStatus.RECORDING,
                            recordTime = if (todo.totalFocusTimeMillis > 0L) {
                                totalFormatTime(todo.totalFocusTimeMillis)
                            } else {
                                null
                            },
                            onCheckedChange = { checked ->
                                todos[index] = todo.copy(isCompleted = checked)
                            },
                            onItemClick = {
                                if (state.status == TimerStatus.RECORDING && !todo.isCompleted) {
                                    selectedIndex = index
                                    todos[index] = todo.copy(totalFocusTimeMillis = recordTime)
                                    viewModel.setTotalRecordTimeMills(recordTime)
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


fun pomodoroFormatTime(millis: Long): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
    return String.format("%02d : %02d", minutes, seconds)
}

fun totalFormatTime(millis: Long): String {
    val hours = TimeUnit.MILLISECONDS.toHours((millis))
    val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
    return String.format("%02d : %02d : %02d", hours, minutes, seconds)
}