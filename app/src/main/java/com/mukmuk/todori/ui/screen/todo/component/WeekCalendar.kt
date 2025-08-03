package com.mukmuk.todori.ui.screen.todo.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.CalendarSelectDay
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Gray
import com.mukmuk.todori.ui.theme.LightGray
import com.mukmuk.todori.ui.theme.UserHalf
import com.mukmuk.todori.ui.theme.UserPrimary
import com.mukmuk.todori.ui.theme.UserTenth
import com.mukmuk.todori.ui.theme.White
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn

@Composable
fun WeekCalendar(
    modifier: Modifier = Modifier,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    studyRecords: Map<LocalDate, Int>
) {
    val startPage = 10_000
    val pagerState = rememberPagerState(pageCount = { Int.MAX_VALUE })
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())


    LaunchedEffect(Unit) {
        pagerState.scrollToPage(startPage)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(White)
            .padding(Dimens.Medium)
            .border(1.dp, LightGray, CircleShape)
            .padding(Dimens.Nano)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
        ) { pageIndex ->
            val weekStart = today.startOfWeek()
                .plus((pageIndex - startPage) * 7, DateTimeUnit.DAY)

            val weekDates = (0..6).map { weekStart.plus(it, DateTimeUnit.DAY) }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                weekDates.forEach { date ->

                    val isSelected = date == selectedDate
                    val studyTime = studyRecords[date] ?: 0
                    val backgroundColor = when {
                        date == today -> Gray
                        studyTime >= 240 -> UserTenth
                        studyTime in 60..239 -> UserHalf
                        studyTime in 1..59 -> UserPrimary
                        else -> Color.Transparent
                    }

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(backgroundColor)
                            .then(
                                if (isSelected) Modifier.border(
                                    2.dp,
                                    CalendarSelectDay,
                                    CircleShape
                                ) else Modifier
                            )
                            .clickable { onDateSelected(date) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = date.dayOfMonth.toString(),
                            color = when {
                                isSelected -> Black
                                date == today -> White
                                date > today -> Gray
                                else -> Black
                            }
                        )
                    }
                }
            }
        }
    }
}


private fun LocalDate.startOfWeek(): LocalDate {
    val dayOfWeek = this.dayOfWeek.isoDayNumber
    return this.minus(dayOfWeek - 1, DateTimeUnit.DAY)
}