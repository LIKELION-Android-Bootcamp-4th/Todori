package com.mukmuk.todori.ui.screen.home.home_setting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.mukmuk.todori.ui.screen.home.PomodoroTimerMode
import com.mukmuk.todori.ui.screen.home.components.PomoModeTextBox
import com.mukmuk.todori.ui.screen.mypage.component.CustomSlider
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Background
import com.mukmuk.todori.ui.theme.DarkGray
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Gray
import com.mukmuk.todori.ui.theme.UserPrimary
import com.mukmuk.todori.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeSettingScreen(navController: NavHostController) {
    val viewModel: HomeSettingViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("타이머 설정", style = AppTextStyle.AppBar) },
                colors = TopAppBarDefaults.topAppBarColors(Background),
                navigationIcon = {
                    IconButton(onClick = {
                        val resultState = viewModel.state.value
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("homeSetting", resultState)
                        navController.navigateUp()
                    }) {
                        Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Back Button")
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
                Text("뽀모도로 타이머", style = AppTextStyle.TitleSmall)
                Switch(
                    checked = state.isPomodoroEnabled,
                    colors = SwitchDefaults.colors(
                        uncheckedThumbColor = DarkGray,
                        uncheckedTrackColor = Gray,
                    ),
                    onCheckedChange = {
                        viewModel.onEvent(HomeSettingEvent.SetPomodoroEnabled(it))
                    }
                )
            }

            ElevatedCard(
                colors = CardDefaults.cardColors(containerColor = White),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Column(
                    modifier = Modifier.padding(Dimens.Medium),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (state.isPomodoroEnabled) {
                        SettingSliderRow(
                            label = PomodoroTimerMode.FOCUSED,
                            value = state.focusMinutes,
                            maxValue = 60,
                            onValueChange = { minutes ->
                                viewModel.onEvent(HomeSettingEvent.SetFocusTime(minutes, 0))
                            }
                        )

                        SettingSliderRow(
                            label = PomodoroTimerMode.SHORT_RESTED,
                            value = state.shortRestMinutes,
                            maxValue = 30,
                            onValueChange = { minutes ->
                                viewModel.onEvent(HomeSettingEvent.SetShortRestTime(minutes, 0))
                            }
                        )

                        SettingSliderRow(
                            label = PomodoroTimerMode.LONG_RESTED,
                            value = state.longRestMinutes,
                            maxValue = 60,
                            onValueChange = { minutes ->
                                viewModel.onEvent(HomeSettingEvent.SetLongRestTime(minutes, 0))
                            }
                        )
                    } else {
                        Text(
                            text = "뽀모도로 타이머를 켜면 설정할 수 있습니다.",
                            style = AppTextStyle.BodySmall,
                            color = DarkGray,
                            modifier = Modifier.padding(vertical = Dimens.Medium)
                        )
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
                    "집중-휴식 4 사이클 완료 후 긴 휴식시간을 가집니다.",
                    style = AppTextStyle.BodySmall
                )
            }
        }
    }
}

@Composable
private fun SettingSliderRow(
    label: PomodoroTimerMode,
    value: Int,
    maxValue: Int,
    onValueChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.Small)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PomoModeTextBox(label)

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "${value}분",
                style = AppTextStyle.Body,
                color = DarkGray
            )
        }

        Spacer(modifier = Modifier.height(Dimens.Tiny))

        CustomSlider(
            value = value.toFloat(),
            onValueChange = { minutes -> onValueChange(minutes.toInt()) },
            valueRange = 0f..maxValue.toFloat(),
            activeColor = UserPrimary,
            inactiveColor = Gray,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
