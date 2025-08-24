package com.mukmuk.todori.data.repository


import android.os.Build
import androidx.annotation.RequiresApi
import com.mukmuk.todori.data.remote.stat.MonthStat
import com.mukmuk.todori.data.service.MonthStatService
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
class MonthStatRepository @Inject constructor(
    private val service: MonthStatService
) {

    suspend fun getMonthStat(uid: String, year: Int, month: Int): MonthStat? {
        return service.getMonthStat(uid, year, month)
    }

    suspend fun getCurrentMonthStat(uid: String): MonthStat? {
        return service.getCurrentMonthStat(uid)
    }
}
