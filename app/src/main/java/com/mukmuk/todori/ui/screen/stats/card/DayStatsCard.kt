package com.mukmuk.todori.ui.screen.stats.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Dimens.DefaultCornerRadius
import com.mukmuk.todori.ui.theme.LightGray
import com.mukmuk.todori.ui.theme.White
import kotlinx.datetime.LocalDate

@Composable
fun DayStatsCard(
    selectedDay: LocalDate,
    studySeconds: Int,
    completedTodos: Int,
    totalTodos:Int,
//    onReflectionChange: (String) -> Unit
    ) {
    var reflection by remember { mutableStateOf("") }
    Card(
        modifier = Modifier
            .fillMaxWidth(1f)
            .padding(horizontal = Dimens.Medium),
        shape = RoundedCornerShape(DefaultCornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = White
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(Dimens.Medium)) {
            //날짜
            Text(
                "${selectedDay.year}년 ${selectedDay.monthNumber}월 ${selectedDay.dayOfMonth}일",
                style = AppTextStyle.TitleSmall
            )
            Spacer(modifier = Modifier.height(Dimens.XXLarge))

            //공부시간
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("공부시간", style = AppTextStyle.Body)
                Text(
                    "${studySeconds / 3600} : ${(studySeconds % 3600) / 60} : ${studySeconds % 60}",
                    style = AppTextStyle.BodyLarge
                )
            }
            Spacer(modifier = Modifier.height(Dimens.XLarge))

            //달성 투두
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("달성 TODO", style = AppTextStyle.Body)
                Text("$completedTodos / $totalTodos", style = AppTextStyle.BodyLarge)
            }
            Spacer(modifier = Modifier.height(Dimens.XLarge))

            //한 줄 회고
            Text("한 줄 회고", style = AppTextStyle.Body)
            Spacer(modifier = Modifier.height(Dimens.Small))
//            Card(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(60.dp),
//                shape = RoundedCornerShape(DefaultCornerRadius),
//                colors = CardDefaults.cardColors(
//                    containerColor = LightGray
//                ),
//            ) {
            //TODO:디자인 변경
                TextField(
                    value = reflection,
                    onValueChange = {
                        if (it.length <= 20) {
//                            onReflectionChange(it)
                        }
                    },
                    placeholder = { Text("한 줄 회고를 작성 해 주세요.") },
                    singleLine = true
                )
//                Box(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(horizontal = Dimens.Medium),
//                    contentAlignment = Alignment.CenterStart
//                ) {
//                    Text("$reflection")
//                }
//            }
            Spacer(modifier = Modifier.height(Dimens.XLarge))

            //투두
            Text("투두 나열")
        }
    }
}