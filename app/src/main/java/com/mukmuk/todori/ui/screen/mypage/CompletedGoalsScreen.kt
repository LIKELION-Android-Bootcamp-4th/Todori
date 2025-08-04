package com.mukmuk.todori.ui.screen.mypage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.data.remote.goal.Goal
import com.mukmuk.todori.ui.component.SimpleTopAppBar
import com.mukmuk.todori.ui.screen.mypage.component.CompletedGoalCard
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.White

@Composable
fun CompletedGoalsScreen(
    onBack: () -> Unit,
    //, user: User
    completedGoals: List<Goal>
    ) {
    Scaffold (
        topBar = {
            SimpleTopAppBar(title = "완료한 목표 ${completedGoals.size}", onBackClick = onBack)
        },
        containerColor = White
    ){ innerPadding ->
        if (completedGoals.isEmpty()) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(White),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "완료한 목표가 없습니다.")
            }
        } else {
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
                    CompletedGoalCard(goal = goal)
                }
                item { Spacer(modifier = Modifier.height(Dimens.Tiny)) }
            }
        }
    }
}