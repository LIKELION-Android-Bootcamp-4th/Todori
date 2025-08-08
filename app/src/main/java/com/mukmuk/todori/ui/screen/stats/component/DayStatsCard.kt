package com.mukmuk.todori.ui.screen.stats.component

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.ImeAction
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
    date: LocalDate,
    record: List<DailyRecord>,
    todos: List<Todo>,
    completedTodos: List<Todo>,
    onReflectionChange: (String) -> Unit
) {
    val dailyRecord = record.firstOrNull()
    val studyTime = dailyRecord?.studyTimeMillis ?: 0L
    val year = date.year
    val month = date.monthValue
    val day = date.dayOfMonth
    val hour = studyTime / 3600
    val minute = (studyTime % 3600) / 60
    val second = studyTime % 60

    var text by remember(dailyRecord?.reflection) { mutableStateOf(dailyRecord?.reflection ?: "") }
    var isEditing by remember { mutableStateOf(false) }

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
                    "$hour : $minute : $second",
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
                    "${completedTodos.count()} / ${todos.size}",
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
                onClick = { isEditing = true }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = Dimens.Medium),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (isEditing) {
                        BasicTextField(
                            value = text,
                            onValueChange = {
                                if (it.length <= 20) {
                                    text = it
                                    onReflectionChange(it)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done), // 엔터 대신 완료 버튼
                            keyboardActions = KeyboardActions {
                                isEditing = false
                            }
                        )
                    } else {
                        Text(
                            text = if (text.isNotBlank()) text else "한 줄 회고를 입력 해 주세요."
                        )
                    }

                }
            }
            Spacer(modifier = Modifier.height(Dimens.XLarge))

            //투두 나열
            Column {
                todos.forEach { todo ->
                    MainTodoItemEditableRow(
                        title = todo.title,
                        isDone = todo.completed,
                        isRecordMode = false,
                        recordTime = null,
                        onCheckedChange = { },
                        onItemClick = { }
                    )
                    Spacer(modifier = Modifier.height(Dimens.Medium))
                }
            }
        }
    }
}