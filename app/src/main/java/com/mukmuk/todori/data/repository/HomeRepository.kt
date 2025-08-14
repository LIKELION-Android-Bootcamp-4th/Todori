package com.mukmuk.todori.data.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.mukmuk.todori.data.remote.dailyRecord.DailyRecord
import com.mukmuk.todori.data.remote.todo.Todo
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

    fun observeDailyRecord(uid: String, onDailyRecordChanged: (List<DailyRecord>) -> Unit) {
        homeService.getDailyRecordCollection(uid)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val dailyRecord = snapshot.documents.mapNotNull { it.toObject(DailyRecord::class.java) }
                    onDailyRecordChanged(dailyRecord)
                } else {
                    onDailyRecordChanged(emptyList())
                }
            }
    }
}