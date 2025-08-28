package com.mukmuk.todori.ui.screen.mypage.studytargets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mukmuk.todori.ui.screen.mypage.component.SimpleConsistencyCheckCard
import com.mukmuk.todori.ui.screen.mypage.component.TargetSettingCard
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.ButtonPrimary
import com.mukmuk.todori.ui.theme.Daily
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Monthly
import com.mukmuk.todori.ui.theme.Weekly
import com.mukmuk.todori.ui.theme.White
import com.mukmuk.todori.util.calculateDailyAchievementRate
import com.mukmuk.todori.util.calculateMonthlyAchievementRate
import com.mukmuk.todori.util.calculateWeeklyAchievementRate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyTargetsScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val viewModel: StudyTargetsViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var tempDailyMinutes by remember(state.targets.dailyMinutes) {
        mutableStateOf(state.targets.dailyMinutes ?: 0)
    }
    var tempWeeklyMinutes by remember(state.targets.weeklyMinutes) {
        mutableStateOf(state.targets.weeklyMinutes ?: 0)
    }
    var tempMonthlyMinutes by remember(state.targets.monthlyMinutes) {
        mutableStateOf(state.targets.monthlyMinutes ?: 0)
    }
    
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is StudyTargetsEffect.SaveSuccess -> {
                    snackbarHostState.showSnackbar("목표가 저장되었어요!")
                }
                is StudyTargetsEffect.ShowMessage -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "목표 시간 설정",
                        style = AppTextStyle.Title
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로가기"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = Dimens.Medium)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Dimens.Medium)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(Dimens.Large)
                )
            }

            Text(
                text = "당신만의 학습 목표를 설정해보세요",
                style = AppTextStyle.Body,
                modifier = Modifier.padding(vertical = Dimens.Small)
            )

            TargetSettingCard(
                title = "월간 목표 설정",
                icon = Icons.Default.CalendarToday,
                iconColor = Monthly,
                backgroundColor = Monthly,
                currentMinutes = tempMonthlyMinutes,
                maxMinutes = StudyTargetsState.MAX_MONTHLY_MINUTES,
                stepMinutes = StudyTargetsState.MONTHLY_STEP,
                onValueChange = { tempMonthlyMinutes = it },
                achievementRate = if (tempMonthlyMinutes > 0) {
                    calculateMonthlyAchievementRate(tempMonthlyMinutes)
                } else null
            )

            TargetSettingCard(
                title = "주간 목표 설정",
                icon = Icons.Default.DateRange,
                iconColor = Weekly,
                backgroundColor = Weekly,
                currentMinutes = tempWeeklyMinutes,
                maxMinutes = StudyTargetsState.MAX_WEEKLY_MINUTES,
                stepMinutes = StudyTargetsState.WEEKLY_STEP,
                onValueChange = { tempWeeklyMinutes = it },
                achievementRate = if (tempWeeklyMinutes > 0) {
                    calculateWeeklyAchievementRate(tempWeeklyMinutes)
                } else null
            )

            TargetSettingCard(
                title = "일간 목표 설정",
                icon = Icons.Default.Today,
                iconColor = Daily,
                backgroundColor = Daily,
                currentMinutes = tempDailyMinutes,
                maxMinutes = StudyTargetsState.MAX_DAILY_MINUTES,
                stepMinutes = StudyTargetsState.DAILY_STEP,
                onValueChange = { tempDailyMinutes = it },
                achievementRate = if (tempDailyMinutes > 0) {
                    calculateDailyAchievementRate(tempDailyMinutes)
                } else null
            )

            if (tempDailyMinutes > 0 || tempWeeklyMinutes > 0 || tempMonthlyMinutes > 0) {
                SimpleConsistencyCheckCard(
                    dailyMinutes = tempDailyMinutes,
                    weeklyMinutes = tempWeeklyMinutes,
                    monthlyMinutes = tempMonthlyMinutes,
                    isExpanded = state.showConsistencyCheck,
                    onToggleExpanded = viewModel::toggleConsistencyCheck
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Dimens.Medium),
                horizontalArrangement = Arrangement.spacedBy(Dimens.Small)
            ) {

                OutlinedButton(
                    onClick = {
                        tempDailyMinutes = (1.8 * 60).toInt()
                        tempWeeklyMinutes = (8.2 * 60).toInt()
                        tempMonthlyMinutes = (32 * 60).toInt()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(Dimens.Small)
                ) {
                    Text(
                        text = "평균값으로 재설정",
                        style = AppTextStyle.BodyBold.copy(color = Black)
                    )
                }

                Button(
                    onClick = {
                        viewModel.updateAllTargets(tempDailyMinutes, tempWeeklyMinutes, tempMonthlyMinutes)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ButtonPrimary
                    ),
                    shape = RoundedCornerShape(Dimens.Small),
                    enabled = !state.isSaving
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(Dimens.Medium),
                            color = Black,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "저장하기",
                            style = AppTextStyle.BodyBold.copy(color = White)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Dimens.Large))
        }
    }
}