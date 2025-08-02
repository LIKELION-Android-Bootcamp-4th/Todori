package com.mukmuk.todori.ui.screen.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mukmuk.todori.ui.screen.home.components.PomoModeTextBox
import com.mukmuk.todori.ui.screen.home.components.TimerTextFieldInput
import com.mukmuk.todori.ui.screen.home.home_setting.HomeSettingState
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Background
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Gray
import com.mukmuk.todori.ui.theme.GroupSecondary
import com.mukmuk.todori.ui.theme.White
import java.util.Calendar
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel, navController: NavHostController) {
    val state by viewModel.state.collectAsState()
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle

    LaunchedEffect(savedStateHandle) {
        val homeSettingState = savedStateHandle?.get<HomeSettingState>("homeSetting")
        if (homeSettingState != null) {
            viewModel.updateInitialTimerSettings(homeSettingState)
            savedStateHandle.remove<HomeSettingState>("homeSetting")
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
                    onClick = { },
                    modifier = Modifier.size(76.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Gray,
                        contentColor = Black
                    ),
                    shape = CircleShape
                ) {
                    Text(text = "기록", style = AppTextStyle.TitleSmall)
                }
                Spacer(modifier = Modifier.width(Dimens.XXLarge))
                if (state.isRunning) {
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
                        onClick = { viewModel.onEvent(TimerEvent.Start) },
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
                    onClick = { },
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