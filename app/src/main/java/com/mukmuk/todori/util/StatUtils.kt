package com.mukmuk.todori.util

import com.github.mikephil.charting.data.BarEntry
import com.mukmuk.todori.data.remote.todo.Todo
import java.time.LocalDate

fun buildBarEntries(
    todos: List<Todo>,
    completedTodos: List<Todo>,
    weekRange: List<LocalDate> // WeekViewModel.getWeekRange() 결과 데이터(일~토)
): Pair<List<BarEntry>, List<BarEntry>> {

    val totalEntries = mutableListOf<BarEntry>()
    val completedEntries = mutableListOf<BarEntry>()

    weekRange.forEachIndexed { index, date ->
        val todosForDay = todos.filter { it.date.toString().startsWith(date.toString()) }
        val completedForDay = completedTodos.filter { it.date.toString().startsWith(date.toString()) }

        totalEntries.add(BarEntry(index.toFloat(), todosForDay.size.toFloat()))
        completedEntries.add(BarEntry(index.toFloat(), completedForDay.size.toFloat()))
    }

    return totalEntries to completedEntries
}
