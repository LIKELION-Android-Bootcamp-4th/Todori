package com.mukmuk.todori.util

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.datetime.LocalDate as KxLocalDate
import java.time.LocalDate as JtLocalDate

@RequiresApi(Build.VERSION_CODES.O)
fun KxLocalDate.toJavaLocalDate(): JtLocalDate =
    JtLocalDate.of(this.year, this.monthNumber, this.dayOfMonth)

fun activeDaysText(days: List<String>): String {
    val allDays = listOf("월", "화", "수", "목", "금", "토", "일")
    val weekdays = listOf("월", "화", "수", "목", "금")
    val weekends = listOf("토", "일")
    val sortedDays = days.sortedBy { allDays.indexOf(it) }

    return when {
        sortedDays == allDays -> "매일"
        sortedDays == weekdays -> "평일"
        sortedDays == weekends -> "주말"
        else -> sortedDays.joinToString(" ")
    }
}
