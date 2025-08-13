package com.mukmuk.todori.ui.screen.mypage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.mukmuk.todori.ui.component.SimpleTopAppBar
import com.mukmuk.todori.ui.screen.mypage.component.PointProgressBar
import com.mukmuk.todori.ui.screen.mypage.component.QuestSection
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.White
import com.mukmuk.todori.util.getLevelInfo

@Composable
fun MyLevelScreen(
    onBack: () -> Unit
) {
    val questViewModel: QuestViewModel = hiltViewModel()
    val ui by questViewModel.ui.collectAsState()
    val uid = Firebase.auth.currentUser?.uid ?: return

    LaunchedEffect(uid) {
        questViewModel.loadDailyQuests(uid)
    }

    Scaffold(
        topBar = { SimpleTopAppBar(title = "나의 레벨", onBackClick = onBack) },
        containerColor = White
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
                .padding(Dimens.Medium)
                .background(color = White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(Dimens.Medium))

                Text(
                    text = "나의 레벨",
                    style = AppTextStyle.Title,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                val levelInfo = getLevelInfo(ui.level)

                Text(
                    text = levelInfo.name,
                    style = AppTextStyle.Timer,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Image(
                    painter = painterResource(id = levelInfo.imageRes),
                    contentDescription = "레벨 이미지",
                    modifier = Modifier.size(228.dp)
                )

                Spacer(modifier = Modifier.height(Dimens.Large))

                // 진행바: current = 현 버킷 포인트, max = current + 남은량
                val current = ui.rewardPoint
                val max = (ui.rewardPoint + ui.nextLevelPoint).coerceAtLeast(1)

                PointProgressBar(
                    level = ui.level,
                    currentPoint = current,
                    maxPoint = max
                )

                Spacer(modifier = Modifier.height(Dimens.Large))

                QuestSection(quests = ui.quests)
            }
        }
    }
}
