package com.mukmuk.todori.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.mukmuk.todori.data.remote.dailyRecord.DailyRecord
import com.mukmuk.todori.data.remote.dailyRecord.ReflectionV2
import com.mukmuk.todori.data.service.DailyRecordService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

class DailyRecordRepository @Inject constructor(
    private val dailyRecordService: DailyRecordService
) {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getTotalStudyTimeMillis(
        uid: String,
        year: Int,
        month: Int
    ): Long = withContext(Dispatchers.Default) {
        val records = dailyRecordService.getRecordsByMonth(uid, year, month)
        records.filter { it.studyTimeMillis > 0L }
            .sumOf { it.studyTimeMillis }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getAverageStudyTimeMillis(
        uid: String,
        year: Int,
        month: Int
    ): Long = withContext(Dispatchers.Default) {
        val records = dailyRecordService.getRecordsByMonth(uid, year, month)
        val studied = records.filter { it.studyTimeMillis > 0L }
        if (studied.isNotEmpty()) studied.sumOf { it.studyTimeMillis } / studied.size else 0L
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getRecordsByWeek(
        uid: String,
        start: LocalDate,
        end: LocalDate
    ): List<DailyRecord> = withContext(Dispatchers.IO) {
        dailyRecordService.getRecordsByWeek(uid, start, end)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getWeeklyTotalStudyTimeMillis(
        uid: String,
        start: LocalDate,
        end: LocalDate
    ): Long = withContext(Dispatchers.Default) {
        val records = getRecordsByWeek(uid, start, end)
        records.filter { it.studyTimeMillis > 0L }
            .sumOf { it.studyTimeMillis }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getWeeklyAverageStudyTimeMillis(
        uid: String,
        start: LocalDate,
        end: LocalDate
    ): Long = withContext(Dispatchers.Default) {
        val records = getRecordsByWeek(uid, start, end)
        val studied = records.filter { it.studyTimeMillis > 0L }
        if (studied.isNotEmpty()) studied.sumOf { it.studyTimeMillis } / studied.size else 0L
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getRecordByDate(uid: String, date: LocalDate): DailyRecord? =
        dailyRecordService.getRecordByDate(uid, date)

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getRecordsByMonth(uid: String, year: Int, month: Int): List<DailyRecord> =
        withContext(Dispatchers.IO) { dailyRecordService.getRecordsByMonth(uid, year, month) }
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateDailyRecord(uid: String, record: DailyRecord) { dailyRecordService.updateDailyRecord(uid, record) }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateReflectionV2(
        uid: String,
        date: LocalDate,
        v2: ReflectionV2
    ) = withContext(Dispatchers.IO) {
        dailyRecordService.updateReflectionV2(uid, date, v2)
    }

}
