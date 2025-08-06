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
    val profileViewModel: ProfileViewModel = hiltViewModel()

    val quests by questViewModel.dailyQuests.collectAsState()
    val profile by profileViewModel.profile.collectAsState()

    LaunchedEffect(Unit) {
        questViewModel.loadDailyQuests("testuser")
        profileViewModel.loadProfile("testuser")
        //questViewModel.loadDailyQuests(uid)
    }

    profile?.let { user ->
        val levelInfo = getLevelInfo(user.level)

        Scaffold(
            topBar = {
                SimpleTopAppBar(title = "나의 레벨", onBackClick = onBack)
            },
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

                    PointProgressBar(
                        level = user.level,
                        currentPoint = 60, //TODO: 경험치통 정하기
                        maxPoint = 100
                    )

                    Spacer(modifier = Modifier.height(Dimens.Large))

                    QuestSection(quests = quests)

                }
            }
        }
    }
}
