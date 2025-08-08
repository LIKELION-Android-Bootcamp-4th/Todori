package com.mukmuk.todori.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.mukmuk.todori.data.service.DailyRecordService
import java.time.LocalDate
import javax.inject.Inject

class DailyRecordRepository @Inject constructor(
    private val dailyRecordService: DailyRecordService
) {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getDailyRecords(uid: String, date: LocalDate) = dailyRecordService.getDailyRecordByDate(uid, date)
}