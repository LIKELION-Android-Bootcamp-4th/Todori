package com.mukmuk.todori.ui.screen.todo.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.UserHalf
import com.mukmuk.todori.ui.theme.UserPrimary
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerBottomSheet(
    show: Boolean,
    onDismissRequest: () -> Unit,
    onConfirm: (LocalDate, LocalDate) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }

    val calendarState = rememberCalendarState(
        startMonth = YearMonth.now().minusMonths(12),
        endMonth = YearMonth.now().plusMonths(12),
        firstVisibleMonth = YearMonth.now(),
        firstDayOfWeek = DayOfWeek.MONDAY
    )

    if (show) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = sheetState
        ) {
            Column(Modifier.padding(Dimens.Medium)) {
                Text("기간 선택", style = AppTextStyle.TitleMedium, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)

                HorizontalCalendar(
                    state = calendarState,
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    dayContent = { day ->
                        val date = day.date
                        val isStart = date == startDate
                        val isEnd = date == endDate
                        val inRange = startDate != null && endDate != null && date > startDate && date < endDate

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .clickable {
                                    if (startDate == null || (startDate != null && endDate != null)) {
                                        startDate = date
                                        endDate = null
                                    } else if (date > startDate) {
                                        endDate = date
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            when {
                                isStart || isEnd -> {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(UserPrimary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = date.dayOfMonth.toString(), style = AppTextStyle.BodySmall)
                                    }
                                }

                                inRange -> {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(4.dp)
                                            .background(UserHalf)
                                    )
                                }

                                else -> {
                                    Text(text = date.dayOfMonth.toString(), style = AppTextStyle.BodySmall)
                                }
                            }
                        }

                    },
                    monthHeader = {
                        Text(
                            text = "${it.yearMonth.year}년 ${it.yearMonth.monthValue}월",
                            modifier = Modifier.padding(8.dp),
                            style = AppTextStyle.Body.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (startDate != null && endDate != null) {
                            onConfirm(startDate!!, endDate!!)
                            onDismissRequest()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("완료")
                }
            }
        }
    }
}

