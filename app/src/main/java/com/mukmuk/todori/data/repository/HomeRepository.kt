package com.mukmuk.todori.data.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.mukmuk.todori.data.remote.dailyRecord.DailyRecord
import com.mukmuk.todori.data.remote.todo.Todo
import com.mukmuk.todori.data.service.HomeService
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.time.LocalDate
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val homeService: HomeService
) {
    suspend fun createDailyRecord(uid: String, dailyRecord: DailyRecord) = homeService.createDailyRecord(uid, dailyRecord)

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getDailyRecord(uid: String, date: LocalDate) = homeService.getDailyRecord(uid, date)

    suspend fun updateDailyRecord(uid: String, data: Map<String, Any>) = homeService.updateDailyRecord(uid, data)

    fun observeDailyRecord(uid: String): Flow<List<DailyRecord>> = callbackFlow {
        val listenerRegistration = homeService.getDailyRecordCollection(uid)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("HomeRepository", "Listen failed.", e)
                    close(e) // 에러 발생 시 Flow 종료
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val dailyRecord = snapshot.documents.mapNotNull { it.toObject(DailyRecord::class.java) }
                    trySend(dailyRecord) // 데이터가 있을 경우 전송
                } else {
                    trySend(emptyList()) // 데이터가 없을 경우 빈 리스트 전송
                }
            }

        // Flow가 취소되면 리스너도 자동으로 제거
        awaitClose { listenerRegistration.remove() }
    }
}