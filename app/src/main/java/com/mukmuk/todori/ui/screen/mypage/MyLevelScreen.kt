package com.mukmuk.todori.ui.screen.mypage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.R
import com.mukmuk.todori.data.dummy.QuestDummy
import com.mukmuk.todori.data.remote.user.User
import com.mukmuk.todori.ui.component.SimpleTopAppBar
import com.mukmuk.todori.ui.screen.mypage.component.PointProgressBar
import com.mukmuk.todori.ui.screen.mypage.component.QuestSection
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Gray
import com.mukmuk.todori.ui.theme.White
import com.mukmuk.todori.util.getLevelInfo

@Composable
fun MyLevelScreen(
    onBack: () -> Unit
    //,user: User
) {
    Scaffold (
        topBar = {
            SimpleTopAppBar(title = "나의 레벨", onBackClick = onBack)
        },
        containerColor = White
    ){ innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
                .padding(Dimens.Medium).background(color = White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //val levelInfo = getLevelInfo(user.level)
            item {
                Spacer(modifier = Modifier.height(Dimens.Medium))

                Text(
                    text = "나의 레벨",
                    style = AppTextStyle.Title,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                )

                Text(
                    text = "꾸준이", //"${levelInfo.name}",
                    style = AppTextStyle.Timer,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                )

                Image(
                    painter = painterResource(id = R.drawable.ic_level3), //(id = levelInfo.imageRes),
                    contentDescription = "레벨 이미지",
                    modifier = Modifier
                        .size(228.dp)
                )

                Spacer(modifier = Modifier.height(Dimens.Large))

                PointProgressBar(// 데이터 일단 더미로..
                    level = 3,
                    currentPoint = 80,
                    maxPoint = 100
                )

                Spacer(modifier = Modifier.height(Dimens.Large))

                QuestSection(quests = QuestDummy.questSample)
            }
        }
    }
}