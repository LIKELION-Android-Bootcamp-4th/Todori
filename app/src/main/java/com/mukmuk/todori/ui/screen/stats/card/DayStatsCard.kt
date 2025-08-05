package com.mukmuk.todori.ui.screen.stats.card

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.data.remote.dailyRecord.DailyRecord
import com.mukmuk.todori.data.remote.todo.Todo
import com.mukmuk.todori.ui.screen.home.components.MainTodoItemEditableRow
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Dimens.DefaultCornerRadius
import com.mukmuk.todori.ui.theme.LightGray
import com.mukmuk.todori.ui.theme.White
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DayStatsCard(
    record: DailyRecord
) {
    val parsedDate = LocalDate.parse(record.date)
    val parseTime = record.studyTimeMillis

    val year = parsedDate.year
    val month = parsedDate.monthValue
    val day = parsedDate.dayOfMonth

    var text by remember { mutableStateOf(record.reflection ?: "") }
    var isEditing by remember { mutableStateOf(false) }

    val todos = listOf(
        Todo(title = "스트레칭 하기", isCompleted = true),
        Todo(title = "스쿼트 50개", isCompleted = false),
        Todo(title = "런닝 30분", isCompleted = true)

    )

    val completedTodos = todos.count { it.isCompleted }

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
                "${year}년 ${month}월 ${day}일",
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
                    "${parseTime / 3600} : ${(parseTime % 3600) / 60} : ${parseTime % 60}",
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
                Text(
                    "$completedTodos / ${todos.size}",
                    style = AppTextStyle.BodyLarge
                )
            }
            Spacer(modifier = Modifier.height(Dimens.XLarge))

            //한 줄 회고
            Text("한 줄 회고", style = AppTextStyle.Body)
            Spacer(modifier = Modifier.height(Dimens.Small))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(DefaultCornerRadius),
                colors = CardDefaults.cardColors(
                    containerColor = LightGray
                ),
                onClick = {}
            ) {
                //TODO:디자인 변경
//                TextField(
//                    value = reflection,
//                    onValueChange = {
//                        if (it.length <= 20) {
////                            onReflectionChange(it)
//                        }
//                    },
//                    placeholder = { Text("한 줄 회고를 작성 해 주세요.") },
//                    singleLine = true
//                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = Dimens.Medium),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (isEditing) {
                        // 텍스트 입력 중
                        BasicTextField(
                            value = text,
                            onValueChange = { text = it },
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        // 편집 전 상태
                        Text(
                            text = if (text.isNotBlank()) text else "한 줄 회고를 입력 해 주세요."
                        )
                    }

                }
            }
            Spacer(modifier = Modifier.height(Dimens.XLarge))

            //투두 나열
//            LazyColumn {
//                itemsIndexed(todos) { index, todo ->
//                    MainTodoItemEditableRow(
//                        title = todo.title,
//                        isDone = todo.isCompleted,
//                        isRecordMode = state.status == TimerStatus.RECORDING,
//                        recordTime = if (todo.totalFocusTimeMillis > 0L) {
//                            totalFormatTime(todo.totalFocusTimeMillis)
//                        } else {
//                            null
//                        },
//                        onCheckedChange = { checked ->
//
//                        },
//                        onItemClick = {}
//                    )
//                    Spacer(modifier = Modifier.height(Dimens.Medium))
//                }
//            }
        }
    }
}