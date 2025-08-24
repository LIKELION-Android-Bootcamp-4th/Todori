package com.mukmuk.todori.util

import android.os.Build
import androidx.annotation.RequiresApi
import com.mukmuk.todori.data.remote.dailyRecord.DailyRecord
import java.time.LocalDate
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

fun buildHeatmapData(
    weekRange: List<LocalDate>,
    dailyRecords: List<DailyRecord>
): List<List<Int>> {
    val timeSlots = listOf(6..8, 9..11, 12..14, 15..17, 18..20, 21..23)

    val rawData = weekRange.map { date ->
        val record = dailyRecords.find { it.date == date.toString() }

        timeSlots.map { slotRange ->
            val totalMinutes = slotRange.sumOf { hour ->
                record?.hourlyMinutes?.get(hour.toString()) ?: 0L
            } / 1000 / 60
            totalMinutes.toInt()
        }
    }

    val maxMinutes = rawData.flatten().maxOrNull() ?: 1

    return rawData.map { dayData ->
        dayData.map { minutes ->
            if (minutes == 0) 0 else (minutes * 100 / maxMinutes)
        }
    }
}

