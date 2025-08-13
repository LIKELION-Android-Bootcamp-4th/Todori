package com.mukmuk.todori.ui.screen.stats.component.day

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.data.remote.todo.Todo
import com.mukmuk.todori.ui.screen.home.components.MainTodoItemEditableRow
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Dimens.DefaultCornerRadius
import com.mukmuk.todori.ui.theme.White
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DayStatsCard(
    selectedDate: LocalDate,
    studyTimeMillis: Long,
    todos: List<Todo>,
) {
    val year = selectedDate.year
    val month = selectedDate.monthValue
    val day = selectedDate.dayOfMonth

    val totalSeconds = studyTimeMillis / 1000
    val hour = totalSeconds / 3600
    val minute = (totalSeconds % 3600) / 60
    val second = totalSeconds % 60

    val completedTodos = todos.count { it.completed }

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
                    "$completedTodos / ${todos.size}",
                    style = AppTextStyle.BodyLarge
                )
            }
            Spacer(modifier = Modifier.height(Dimens.XLarge))

            //투두 나열
            Column {
                todos.take(3).forEach { todo ->
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