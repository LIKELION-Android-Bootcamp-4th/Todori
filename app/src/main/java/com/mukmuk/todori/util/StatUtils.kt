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

