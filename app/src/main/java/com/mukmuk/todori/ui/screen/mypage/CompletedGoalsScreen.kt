package com.mukmuk.todori.ui.screen.mypage

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.auth.FirebaseAuth
import com.mukmuk.todori.ui.component.SimpleTopAppBar
import com.mukmuk.todori.ui.screen.mypage.component.CompletedGoalCard
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.White

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CompletedGoalsScreen(onBack: () -> Unit) {
    val viewModel: CompletedGoalsViewModel = hiltViewModel()
    val completedGoals by viewModel.goals.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
    LaunchedEffect(Unit) {
        viewModel.loadCompletedGoals(uid.toString())
    }

    Scaffold(
        topBar = {
            SimpleTopAppBar(
                title = "완료한 목표",
                onBackClick = onBack)
        },
        containerColor = White
    ) { innerPadding ->
        when {
            isLoading -> {
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(White),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.material3.CircularProgressIndicator()
                }
            }

            completedGoals.isEmpty() -> {
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "완료한 목표가 없습니다.")
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            top = innerPadding.calculateTopPadding(),
                            start = Dimens.Medium,
                            end = Dimens.Medium,
                            bottom = Dimens.Medium
                        )
                        .background(color = White),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(completedGoals) { goal ->
                        goal?.let {
                            CompletedGoalCard(goal = goal)
                        }
                    }
                    item { Spacer(modifier = Modifier.height(Dimens.Tiny)) }
                }
            }
        }
    }
}