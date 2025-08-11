package com.mukmuk.todori.data.service

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.FirebaseFirestore
import com.mukmuk.todori.data.remote.dailyRecord.DailyRecord
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

class DailyRecordService @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getRecordsByMonth(uid: String, year: Int, month: Int): List<DailyRecord> {
        val ym = YearMonth.of(year, month)
        val startDay = ym.atDay(1).toString()
        val endDay = ym.atEndOfMonth().toString()

        val snapshot = firestore.collection("users")
            .document(uid)
            .collection("dailyRecord")
            .whereGreaterThanOrEqualTo("date", startDay)
            .whereLessThanOrEqualTo("date", endDay)
            .get()
            .await()

        return snapshot.toObjects(DailyRecord::class.java)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getRecordsByWeek(uid: String, start: LocalDate, end: LocalDate): List<DailyRecord> {
        val startDay = start.toString()
        val endDay = end.toString()

        val snapshot = firestore.collection("users")
            .document(uid)
            .collection("dailyRecord")
            .whereGreaterThanOrEqualTo("date", startDay)
            .whereLessThanOrEqualTo("date", endDay)
            .get()
            .await()

        return snapshot.toObjects(DailyRecord::class.java)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getRecordByDate(uid: String, date: LocalDate): DailyRecord? {
        val snapshot = firestore.collection("users")
            .document(uid)
            .collection("dailyRecord")
            .document(date.toString())
            .get()
            .await()

        return snapshot.toObject(DailyRecord::class.java)
    }
}
