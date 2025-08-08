package com.mukmuk.todori.data.service

import com.google.firebase.firestore.FirebaseFirestore
import com.mukmuk.todori.data.remote.dailyRecord.DailyRecord
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class DailyRecordService @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    suspend fun getRecordsByMonth(uid: String, year: Int, month: Int): List<DailyRecord> {
        val calendar = Calendar.getInstance()

        calendar.set(year, month - 1, 1)
        val startDate = calendar.time
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        val endDate = calendar.time

        val startDay = dateFormat.format(startDate)
        val endDay = dateFormat.format(endDate)

        val snapshot = firestore.collection("users")
            .document(uid)
            .collection("dailyRecord")
            .whereGreaterThanOrEqualTo("date", startDay)
            .whereLessThanOrEqualTo("date", endDay)
            .get()
            .await()

        return snapshot.toObjects(DailyRecord::class.java)
    }
}
