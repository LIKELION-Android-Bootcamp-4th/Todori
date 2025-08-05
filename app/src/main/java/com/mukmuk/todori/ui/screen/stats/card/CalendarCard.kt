package com.mukmuk.todori.ui.screen.stats.card

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.mukmuk.todori.data.remote.dailyRecords.DailyRecords
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.CalendarSelectDay
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.White
import com.mukmuk.todori.ui.theme.Dimens.DefaultCornerRadius
import com.mukmuk.todori.ui.theme.Gray
import com.mukmuk.todori.ui.theme.UserHalf
import com.mukmuk.todori.ui.theme.UserPrimary
import com.mukmuk.todori.ui.theme.UserTenth
import java.time.LocalDate
import java.time.YearMonth

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarCard(
    record: List<DailyRecords>,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.Medium),
        shape = RoundedCornerShape(DefaultCornerRadius),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {

        val calendarState = rememberCalendarState(
            startMonth = YearMonth.now().minusMonths(12),
            endMonth = YearMonth.now().plusMonths(12),
            firstVisibleMonth = YearMonth.now(),
            firstDayOfWeek = java.time.DayOfWeek.MONDAY
        )

        HorizontalCalendar(
            modifier = Modifier.padding(Dimens.Medium),
            state = calendarState,
            contentPadding = PaddingValues(horizontal = 8.dp),
            dayContent = { day ->
                val date = day.date
                val isSelected = date == selectedDate
//                val backgroundColor = when {
//                    date ==
//                    record[0].studySeconds >= 240 -> UserTenth
//                    record[0].studySeconds >= in 60..239 -> UserHalf
//                    record[0].studySeconds >= in 1..59 -> UserPrimary
//                    else -> Color.Transparent
//                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .clickable { onDateSelected(date) },
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .border(1.dp, CalendarSelectDay, CircleShape)
                            ,
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = date.dayOfMonth.toString(), style = AppTextStyle.BodySmall)
                        }
                    } else {
                        Text(text = date.dayOfMonth.toString(), style = AppTextStyle.BodySmall)
                    }
                }
            },
            monthHeader = {
                Text(
                    text = "${it.yearMonth.year}년 ${it.yearMonth.monthValue}월",
                    modifier = Modifier.padding(8.dp),
                    style = AppTextStyle.TitleSmall
                )
            }
        )
    }
}