package com.mukmuk.todori.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.mukmuk.todori.data.remote.dailyRecord.DailyRecord
import com.mukmuk.todori.data.service.HomeService
import java.time.LocalDate
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val homeService: HomeService
) {
    suspend fun createDailyRecord(uid: String, dailyRecord: DailyRecord) = homeService.createDailyRecord(uid, dailyRecord)

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getDailyRecord(uid: String, date: LocalDate) = homeService.getDailyRecord(uid, date)

    suspend fun updateDailyRecord(uid: String, dailyRecord: DailyRecord) = homeService.updateDailyRecord(uid, dailyRecord)
}