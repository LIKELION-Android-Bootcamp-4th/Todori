package com.mukmuk.todori.ui.screen.stats.component.day

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.DayPosition
import com.mukmuk.todori.data.remote.dailyRecord.DailyRecord
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.CalendarSelectDay
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Dimens.DefaultCornerRadius
import com.mukmuk.todori.ui.theme.UserHalf
import com.mukmuk.todori.ui.theme.UserPrimary
import com.mukmuk.todori.ui.theme.UserTenth
import com.mukmuk.todori.ui.theme.White
import kotlinx.datetime.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarCard(
    record: List<DailyRecord>,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onMonthChanged: (YearMonth) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.Medium),
        shape = RoundedCornerShape(DefaultCornerRadius),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {

        val firstDay = DayOfWeek.SUNDAY
        val daysOfWeek = remember { (0..6).map { firstDay.plus(it.toLong()) } }

        val calendarState = rememberCalendarState(
            startMonth = YearMonth.now().minusMonths(12),
            endMonth = YearMonth.now().plusMonths(12),
            firstVisibleMonth = YearMonth.now(),
            firstDayOfWeek = firstDay
        )

        LaunchedEffect(calendarState) {
            snapshotFlow { calendarState.firstVisibleMonth.yearMonth }
                .collect { onMonthChanged(it) }
        }

        val currentYm by remember(calendarState) {
            derivedStateOf { calendarState.firstVisibleMonth.yearMonth }
        }
        val targetYm = remember(selectedDate) {
            YearMonth.of(selectedDate.year, selectedDate.monthValue)
        }

        LaunchedEffect(targetYm) {
            if (!calendarState.isScrollInProgress && currentYm != targetYm) {
                calendarState.scrollToMonth(targetYm)
            }
        }

        HorizontalCalendar(
            modifier = Modifier.padding(Dimens.Medium),
            state = calendarState,
            monthHeader = {
                Row(Modifier.fillMaxWidth().padding(bottom = Dimens.Small)) {
                    daysOfWeek.forEach { dow ->
                        val label = dow.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                            .uppercase(Locale.US)
                        Text(
                            text = label,
                            style = AppTextStyle.BodySmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            },
            dayContent = { day ->
                val date = day.date
                val isSelected = date == selectedDate

                val matchedRecord = record.find { it.date == date.toString() }
                val studyMillis = matchedRecord?.studyTimeMillis ?: 0L
                val studySec = (studyMillis / 1000).toInt()

                val backgroundColor = when {
                    studySec in 1..7200 -> UserTenth  //~2시간
                    studySec in 7201..21600 -> UserHalf  //2~6시간
                    studySec >= 21601 -> UserPrimary  //6시간~
                    else -> Color.Transparent
                }

                if (day.position == DayPosition.MonthDate) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = Dimens.Small, horizontal = Dimens.Nano)
                            .clip(CircleShape)
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
                                    .border(2.dp, CalendarSelectDay, CircleShape),
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
        )
    }
}