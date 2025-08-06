package com.mukmuk.todori.ui.screen.stats.component

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.DayPosition
import com.mukmuk.todori.data.remote.dailyRecord.DailyRecord
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.CalendarSelectDay
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.White
import com.mukmuk.todori.ui.theme.Dimens.DefaultCornerRadius
import com.mukmuk.todori.ui.theme.Gray
import com.mukmuk.todori.ui.theme.UserHalf
import com.mukmuk.todori.ui.theme.UserPrimary
import com.mukmuk.todori.ui.theme.UserTenth
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import java.time.LocalDate
import java.time.YearMonth

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarCard(
    record: List<DailyRecord>,
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
//            contentPadding = PaddingValues(horizontal = 8.dp),
            dayContent = { day ->
                val date = day.date
                val isSelected = date == selectedDate

                val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
                val matchedRecord = record.find { it.date == date.toString() }
                val studyTime = matchedRecord?.studyTimeMillis ?: 0

                val backgroundColor = when {
                    date == today -> Gray
                    studyTime in 1..7200 -> UserTenth
                    studyTime in 7201..21600 -> UserHalf
                    studyTime >= 21601 -> UserPrimary
                    else -> Color.Transparent
                }

                if (day.position == DayPosition.MonthDate) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = Dimens.Small, horizontal = Dimens.Nano)
                            .clip(CircleShape)
//                        .height(45.dp)
                            .size(40.dp)
                            .clickable { onDateSelected(date) }
                            .clip(CircleShape)
                            .background(backgroundColor),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, CalendarSelectDay, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = date.dayOfMonth.toString(),
                                    style = AppTextStyle.BodySmall
                                )
                            }
                        } else {
                            Text(text = date.dayOfMonth.toString(), style = AppTextStyle.BodySmall)
                        }
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