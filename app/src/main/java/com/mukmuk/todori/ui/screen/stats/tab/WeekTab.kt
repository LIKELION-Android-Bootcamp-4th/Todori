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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.ui.screen.stats.card.WeekCard
import com.mukmuk.todori.ui.screen.stats.card.WeekGraph
import com.mukmuk.todori.ui.screen.stats.card.WeekProgress
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.Dimens
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.DatePeriod

@Composable
fun WeekTab(
    selectedWeek: LocalDate,
    onWeekChange: (LocalDate) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
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
                    IconButton(onClick = {
                        onWeekChange(selectedWeek.minus(DatePeriod(days = 7)))
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null,modifier = Modifier.size(24.dp), tint = Black)

                    }
                }

                //주차 표시
                Row(
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Text(
                        "${selectedWeek.year}년 ${selectedWeek.monthNumber}월" +
                                " ${(selectedWeek.dayOfMonth - 1) / 7 + 1}주차",
                        style = AppTextStyle.TitleSmall
                    )
                }

                //오른쪽 화살표
                Row(
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    IconButton(onClick = {
                        onWeekChange(selectedWeek.plus(DatePeriod(days = 7)))
                    }) {
                        Icon(Icons.Default.ArrowForward, contentDescription = null,modifier = Modifier.size(24.dp), tint = Black)
                    }
                }
            }

            WeekCard()
            Spacer(modifier = Modifier.height(Dimens.Large))
            WeekGraph()
            Spacer(modifier = Modifier.height(Dimens.Large))
            WeekProgress(completedTodos = 8, todos = 10)
        }
    }
}