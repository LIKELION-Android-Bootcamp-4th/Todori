package com.mukmuk.todori.ui.screen.stats.tab

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.R
import com.mukmuk.todori.ui.screen.stats.card.MonthCard
import com.mukmuk.todori.ui.screen.stats.card.MonthProgress
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Dimens
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.mukmuk.todori.ui.theme.Black
import java.time.LocalDate

@Composable
fun MonthTab() {
    var selectedMonth by remember {
        mutableStateOf(LocalDate.now())
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.Medium)
        ) {
            //왼쪽 화살표
            Row(
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                IconButton(onClick = {selectedMonth = selectedMonth.minusMonths(1)}) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null,modifier = Modifier.size(24.dp), tint = Black)
                }
            }

            //월 표시
            Row(
                modifier = Modifier.align(Alignment.Center)
            ) {
                Text("${selectedMonth.year}년 ${selectedMonth.monthValue}월", style = AppTextStyle.TitleSmall)
            }

            //오른쪽 화살표
            Row(
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                IconButton(onClick = {selectedMonth = selectedMonth.plusMonths(1)}) {
                    Icon(Icons.Default.ArrowForward, contentDescription = null,modifier = Modifier.size(24.dp), tint = Black)
                }
            }
        }
        MonthCard()
        Spacer(modifier = Modifier.height(Dimens.Large))
        MonthProgress()
    }
}