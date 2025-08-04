package com.mukmuk.todori.ui.screen.home.home_setting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer // Spacer 추가
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height // height Spacer용
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mukmuk.todori.ui.screen.home.PomodoroTimerMode
import com.mukmuk.todori.ui.screen.home.components.PomoModeTextBox
import com.mukmuk.todori.ui.screen.home.components.TimerTextFieldInput
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Background
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.White // White는 Color 타입이어야 합니다.
import java.lang.reflect.Array.set

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeSettingScreen(viewModel: HomeSettingViewModel, navController: NavHostController) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "타이머 설정",
                        style = AppTextStyle.AppBar
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(Background),
                navigationIcon = {
                    IconButton(onClick = {
                        val resultState = viewModel.state.value

                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("homeSetting", resultState)

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
                .padding(innerPadding)
                .padding(horizontal = Dimens.Medium)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Dimens.Medium),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "뽀모도로 타이머",
                    style = AppTextStyle.TitleSmall
                )
                Switch(
                    checked = state.isPomodoroEnabled,
                    onCheckedChange = {
                        viewModel.onEvent(HomeSettingEvent.SetPomodoroEnabled(it))
                    }
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
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
                        modifier = Modifier.padding(Dimens.Medium),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val innerRowPadding = Dimens.Small

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = innerRowPadding),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            PomoModeTextBox(PomodoroTimerMode.FOCUSED)
                            TimerTextFieldInput(
                                initialMinutes = state.focusMinutes,
                                initialSeconds = state.focusSeconds,
                                onTimeChanged = { m, s ->
                                    viewModel.onEvent(HomeSettingEvent.SetFocusTime(m, s))
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(Dimens.Small))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = innerRowPadding),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            PomoModeTextBox(PomodoroTimerMode.SHORT_RESTED)
                            TimerTextFieldInput(
                                initialMinutes = state.shortRestMinutes,
                                initialSeconds = state.shortRestSeconds,
                                onTimeChanged = { m, s ->
                                    viewModel.onEvent(HomeSettingEvent.SetShortRestTime(m, s))
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(Dimens.Small))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = innerRowPadding),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            PomoModeTextBox(PomodoroTimerMode.LONG_RESTED)
                            TimerTextFieldInput(
                                initialMinutes = state.longRestMinutes,
                                initialSeconds = state.longRestSeconds,
                                onTimeChanged = { m, s ->
                                    viewModel.onEvent(HomeSettingEvent.SetLongRestTime(m, s))
                                }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(Dimens.Small))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Dimens.Small),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "집중-휴식 4 사이클 완료 후 긴 휴식시간을 가집니다.",
                    style = AppTextStyle.BodySmall
                )
            }
        }
    }
}