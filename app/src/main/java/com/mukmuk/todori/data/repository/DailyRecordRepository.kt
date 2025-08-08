package com.mukmuk.todori.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.mukmuk.todori.data.remote.dailyRecord.DailyRecord
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
    suspend fun getDailyRecords(uid: String, date: LocalDate) = dailyRecordService.getDailyRecordByDate(uid, date)
    suspend fun updateDailyRecord(uid: String, record: DailyRecord) { dailyRecordService.updateDailyRecord(uid, record) }
}
