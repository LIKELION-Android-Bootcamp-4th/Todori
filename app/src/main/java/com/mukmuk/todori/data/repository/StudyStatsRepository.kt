package com.mukmuk.todori.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.mukmuk.todori.data.remote.study.TodoProgress
import com.mukmuk.todori.data.service.StudyService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

class StudyStatsRepository @Inject constructor(
    private val studyService: StudyService
) {

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getCompletedStudyTodosCount(uid: String, year: Int, month: Int): Int =
        withContext(Dispatchers.Default) {
            studyService.getMyAllProgresses(uid)
                .count { it.done && it.isSameMonth(year, month) }
        }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getTotalStudyTodosCount(uid: String, year: Int, month: Int): Int =
        withContext(Dispatchers.Default) {
            studyService.getMyAllProgresses(uid)
                .count { it.isSameMonth(year, month) }
        }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun TodoProgress.isSameMonth(targetYear: Int, targetMonth: Int): Boolean {
        return runCatching {
            val parsed = LocalDate.parse(date)
            parsed.year == targetYear && parsed.monthValue == targetMonth
        }.getOrDefault(false)
    }
}
