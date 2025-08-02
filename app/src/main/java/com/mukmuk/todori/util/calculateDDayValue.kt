package com.mukmuk.todori.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
fun calculateDDayValue(dueDateString: String): Int? {
    return try {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val dueDate = LocalDate.parse(dueDateString, formatter)
        val today = LocalDate.now()
        ChronoUnit.DAYS.between(today, dueDate).toInt()
    } catch (e: Exception) {
        null
    }
}

