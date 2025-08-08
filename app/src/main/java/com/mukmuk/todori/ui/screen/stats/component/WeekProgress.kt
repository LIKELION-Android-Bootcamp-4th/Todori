package com.mukmuk.todori.ui.screen.stats.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
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
import com.mukmuk.todori.data.remote.dailyRecord.DailyRecord
import com.mukmuk.todori.data.remote.todo.Todo
import com.mukmuk.todori.ui.component.ProgressWithText
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Dimens.DefaultCornerRadius
import com.mukmuk.todori.ui.theme.UserPrimary
import com.mukmuk.todori.ui.theme.White
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeekProgress(
    week: LocalDate,
    allTodos: List<Todo>,
    completedTodos: List<Todo>
) {
    Column() {

        val weekDays = listOf("일", "월", "화", "수", "목", "금", "토")

        val sunday = week.minusDays(week.dayOfWeek.value % 7L)
        val thisWeekDates = (0..6).map { sunday.plusDays(it.toLong()) }

        val dailyProgress = thisWeekDates.map { date ->
            val dailyTotal = allTodos.count { it.date == date.toString() }
            val dailyCompleted = completedTodos.count { it.date == date.toString() }
            Pair(dailyCompleted, dailyTotal)
        }

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
                Text("주간 TODO 통계", style = AppTextStyle.TitleSmall)
                Spacer(modifier = Modifier.height(Dimens.XLarge))

                weekDays.forEachIndexed { index, dayLabel ->
                    val dayRecord = dailyProgress.getOrNull(index)
                    val (completed, total) = dayRecord ?: Pair(0, 0)

                    ProgressWithText(
                        progress = if (total != 0) completed.toFloat() / total else 0f,
                        completed = completed,
                        total = total,
                        progressColor = UserPrimary,
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = Dimens.Nano,
                        label = dayLabel
                    )

                    Spacer(modifier = Modifier.height(Dimens.Large))
                }

            }
        }
        Spacer(modifier = Modifier.height(Dimens.Large))
    }
}