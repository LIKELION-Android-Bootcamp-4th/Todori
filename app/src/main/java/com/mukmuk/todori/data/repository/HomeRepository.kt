package com.mukmuk.todori.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.mukmuk.todori.data.remote.dailyRecord.DailyRecord
import com.mukmuk.todori.data.service.HomeService
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.time.LocalDate
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val homeService: HomeService
) {

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getDailyRecord(uid: String, date: LocalDate) = homeService.getDailyRecord(uid, date)

    suspend fun updateDailyRecord(uid: String, data: Map<String, Any>) =
        homeService.updateDailyRecord(uid, data)

    fun observeDailyRecord(uid: String): Flow<List<DailyRecord>> = callbackFlow {
        val listenerRegistration = homeService.getDailyRecordCollection(uid)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val dailyRecord =
                        snapshot.documents.mapNotNull { it.toObject(DailyRecord::class.java) }
                    trySend(dailyRecord)
                } else {
                    trySend(emptyList())
                }
            }

        awaitClose { listenerRegistration.remove() }
    }
}