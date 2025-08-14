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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mukmuk.todori.data.remote.todo.Todo
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Dimens
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

    val completedTodos = todos.count { it.completed }

    Column(modifier = Modifier.padding(Dimens.Medium)) {
        Text(
            "${year}년 ${month}월 ${day}일",
            style = AppTextStyle.TitleSmall
        )
        Spacer(modifier = Modifier.height(Dimens.Small))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.Small)
        ) {
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                Text("공부시간", style = AppTextStyle.BodySmall)
                Spacer(modifier = Modifier.width(Dimens.Large))
                Text(
                    "$hour h $minute m",
                    style = AppTextStyle.BodyLarge,
                )
            }
            Spacer(modifier = Modifier.width(Dimens.Large))
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                Text("TODO", style = AppTextStyle.BodySmall)
                Spacer(modifier = Modifier.width(Dimens.Large))
                Text(
                    "$completedTodos / ${todos.size}",
                    style = AppTextStyle.BodyLarge
                )
            }
        }
    }

}