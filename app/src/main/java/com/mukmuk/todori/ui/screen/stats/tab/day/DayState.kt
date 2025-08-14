package com.mukmuk.todori.ui.screen.stats.tab.day

import android.os.Build
import androidx.annotation.RequiresApi
import com.mukmuk.todori.data.remote.dailyRecord.DailyRecord
import com.mukmuk.todori.data.remote.dailyRecord.ReflectionV2
import com.mukmuk.todori.data.remote.todo.Todo
import com.mukmuk.todori.util.ReflectionState
import com.mukmuk.todori.util.buildReflection
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
data class DayState(
    val selectedDate: LocalDate = LocalDate.now(),
    val monthRecords: List<DailyRecord> = emptyList(),
    val selectedRecord: DailyRecord? = null,
    val todos: List<Todo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val recordForSelectedDate: DailyRecord?
        get() = monthRecords.firstOrNull { record ->
            runCatching { LocalDate.parse(record.date.trim()) }
                .getOrNull() == selectedDate
        } ?: selectedRecord

    val currentReflectionV2: ReflectionV2?
        get() = recordForSelectedDate?.reflectionV2

    val currentReflectionPreview: String
        get() = currentReflectionV2?.let {
            buildReflection(
                ReflectionState(
                    good = it.good.orEmpty(),
                    improve = it.improve.orEmpty(),
                    blocker = it.blocker.orEmpty()
                )
            )
        }.orEmpty()

    val studyTimeMillis: Long
        get() = recordForSelectedDate?.studyTimeMillis ?: 0L
}