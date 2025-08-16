package com.mukmuk.todori.data.repository

import com.mukmuk.todori.data.remote.stat.DayStat
import com.mukmuk.todori.data.remote.stat.Stats
import com.mukmuk.todori.data.service.DayStatsService
import java.time.LocalDate
import javax.inject.Inject

class DayStatsRepository @Inject constructor(
    private val service: DayStatsService
) {
    suspend fun getDayStat(uid: String, date: LocalDate): DayStat? =
        service.getDayStat(uid, date)

    suspend fun getStats(uid: String): Stats? =
        service.getStats(uid)
}