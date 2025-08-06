package com.mukmuk.todori.ui.screen.mypage

import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import com.mukmuk.todori.data.remote.goal.Goal
import com.mukmuk.todori.ui.component.SimpleTopAppBar
import com.mukmuk.todori.ui.screen.mypage.component.CompletedGoalCard
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.White

@Composable
fun CompletedGoalsScreen(onBack: () -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val uid = "testuser"
    val goals = remember { mutableStateListOf<Goal>() }

    LaunchedEffect(Unit) {
        db.collection("users")
            .document(uid)
            .collection("goals")
            .whereEqualTo("completed", true)
            .get()
            .addOnSuccessListener { result ->
                goals.clear()
                for (document in result) {
                    val goal = document.toObject(Goal::class.java)
                    goals.add(goal)
                }
            }
            .addOnFailureListener {
                Log.d("completedGoalsScreen","완료 목표 불러오기 실패")
            }
    }

    Scaffold (
        topBar = {
            SimpleTopAppBar(title = "완료한 목표 ${goals.size}", onBackClick = onBack)
        },
        containerColor = White
    ){ innerPadding ->
        if (goals.isEmpty()) {
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
                items(goals) { goal ->
                    CompletedGoalCard(goal = goal)
                }
                item { Spacer(modifier = Modifier.height(Dimens.Tiny)) }
            }
        }
    }
}