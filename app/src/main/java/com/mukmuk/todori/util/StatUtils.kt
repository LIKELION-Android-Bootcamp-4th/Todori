package com.mukmuk.todori.util

import com.github.mikephil.charting.data.BarEntry
import com.mukmuk.todori.data.remote.dailyRecord.DailyRecord
import com.mukmuk.todori.data.remote.todo.Todo
import java.time.LocalDate

fun buildBarEntries(
    todos: List<Todo>,
    completedTodos: List<Todo>,
    weekRange: List<LocalDate>
): Pair<List<BarEntry>, List<BarEntry>> {

    val totalEntries = mutableListOf<BarEntry>()
    val completedEntries = mutableListOf<BarEntry>()

    weekRange.forEachIndexed { index, date ->
        val todosForDay = todos.filter { it.date.toString().startsWith(date.toString()) }
        val completedForDay =
            completedTodos.filter { it.date.toString().startsWith(date.toString()) }

        totalEntries.add(BarEntry(index.toFloat(), todosForDay.size.toFloat()))
        completedEntries.add(BarEntry(index.toFloat(), completedForDay.size.toFloat()))
    }

    return totalEntries to completedEntries
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

fun formatHoursOrDash(millis: Long): String {
    val hours = millis / 1000.0 / 60.0 / 60.0
    return if (hours == 0.0) "-" else String.format("%.1fh", hours)
}


fun calculateDailyAchievementRate(minutes: Int): Float {
    val hours = minutes / 60f
    return when {
        hours <= 0 -> 0f
        hours <= 1f -> 0.95f
        hours <= 2f -> 0.85f + (2f - hours) * 0.10f
        hours <= 3f -> 0.70f + (3f - hours) * 0.15f
        hours <= 4f -> 0.55f + (4f - hours) * 0.15f
        hours <= 6f -> 0.35f + (6f - hours) * 0.10f
        hours <= 8f -> 0.20f + (8f - hours) * 0.075f
        else -> 0.15f
    }.coerceIn(0.1f, 0.95f)
}


fun calculateWeeklyAchievementRate(minutes: Int): Float {
    val hours = minutes / 60f
    return when {
        hours <= 0 -> 0f
        hours <= 7f -> 0.90f
        hours <= 14f -> 0.80f + (14f - hours) * 0.10f / 7f
        hours <= 21f -> 0.65f + (21f - hours) * 0.15f / 7f
        hours <= 28f -> 0.45f + (28f - hours) * 0.20f / 7f
        hours <= 35f -> 0.30f + (35f - hours) * 0.15f / 7f
        else -> 0.25f
    }.coerceIn(0.15f, 0.90f)
}

fun calculateMonthlyAchievementRate(minutes: Int): Float {
    val hours = minutes / 60f
    return when {
        hours <= 0 -> 0f
        hours <= 20f -> 0.85f
        hours <= 40f -> 0.80f + (40f - hours) * 0.05f / 20f
        hours <= 60f -> 0.70f + (60f - hours) * 0.10f / 20f
        hours <= 80f -> 0.55f + (80f - hours) * 0.15f / 20f
        hours <= 100f -> 0.40f + (100f - hours) * 0.15f / 20f
        hours <= 120f -> 0.30f + (120f - hours) * 0.10f / 20f
        else -> 0.25f
    }.coerceIn(0.15f, 0.85f)
}

