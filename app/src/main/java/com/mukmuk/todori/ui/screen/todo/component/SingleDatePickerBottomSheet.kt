package com.mukmuk.todori.ui.screen.todo.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.UserPrimary
import com.mukmuk.todori.ui.theme.White
import kotlinx.datetime.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SingleDatePickerBottomSheet(
    show: Boolean,
    onDismissRequest: () -> Unit,
    onConfirm: (LocalDate) -> Unit
) {
    if (!show) return

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    val today = remember { LocalDate.now() }
    var selectedDate by remember { mutableStateOf(today) }

    val currentMonth = remember(selectedDate) {
        "${selectedDate.year}년 ${selectedDate.monthValue}월"
    }
    val currentYearMonth = YearMonth.of(today.year, today.month)
    val calendarState = rememberCalendarState(
        startMonth = currentYearMonth.minusMonths(12),
        endMonth = currentYearMonth.plusMonths(12),
        firstVisibleMonth = currentYearMonth,
        firstDayOfWeek = DayOfWeek.MONDAY
    )
    val currentMonthText = "${calendarState.firstVisibleMonth.yearMonth.year}년 ${calendarState.firstVisibleMonth.yearMonth.monthValue}월"



    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = White
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("날짜 선택", style = AppTextStyle.Body)

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = currentMonthText,
                style = AppTextStyle.Body.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            // 선택 달력
            HorizontalCalendar(
                state = calendarState,
                dayContent = { day ->
                    val isSelected = day.date == selectedDate
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(36.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) UserPrimary else White)
                            .clickable { selectedDate = day.date },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day.date.dayOfMonth.toString(),
                            style = AppTextStyle.BodySmall,
                            color = if (isSelected) White else Black
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    onConfirm(selectedDate)
                    onDismissRequest()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("확인")
            }
        }
    }
}
