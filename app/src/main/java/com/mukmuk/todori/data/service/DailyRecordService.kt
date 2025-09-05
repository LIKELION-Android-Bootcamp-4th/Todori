package com.mukmuk.todori.data.service

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.mukmuk.todori.data.remote.dailyRecord.DailyRecord
import com.mukmuk.todori.data.remote.dailyRecord.ReflectionV2
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
class DailyRecordService @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    private fun userDailyRecordRef(uid: String) =
        firestore.collection("users").document(uid).collection("dailyRecord")

    suspend fun getRecordsByMonth(uid: String, year: Int, month: Int): List<DailyRecord> {
        val ym = YearMonth.of(year, month)
        val startDay = ym.atDay(1).toString()
        val endDay = ym.atEndOfMonth().toString()

        val snapshot = userDailyRecordRef(uid)
            .whereGreaterThanOrEqualTo("date", startDay)
            .whereLessThanOrEqualTo("date", endDay)
            .get()
            .await()

        return snapshot.toObjects(DailyRecord::class.java)
    }

    suspend fun getRecordsByWeek(uid: String, start: LocalDate, end: LocalDate): List<DailyRecord> {
        val startDay = start.toString()
        val endDay = end.toString()

        val snapshot = userDailyRecordRef(uid)
            .whereGreaterThanOrEqualTo("date", startDay)
            .whereLessThanOrEqualTo("date", endDay)
            .get()
            .await()

        return snapshot.toObjects(DailyRecord::class.java)
    }

    suspend fun getRecordByDate(uid: String, date: LocalDate): DailyRecord? {
        val snapshot = userDailyRecordRef(uid)
            .document(date.toString())
            .get()
            .await()

        return snapshot.toObject(DailyRecord::class.java)
    }

    suspend fun updateReflectionV2(
        uid: String,
        date: LocalDate,
        v2: ReflectionV2
    ) {
        val docRef = userDailyRecordRef(uid).document(date.toString())

        val child = mutableMapOf<String, Any>()
        v2.good?.takeIf { it.isNotBlank() }?.let { child["good"] = it }
        v2.improve?.takeIf { it.isNotBlank() }?.let { child["improve"] = it }
        v2.blocker?.takeIf { it.isNotBlank() }?.let { child["blocker"] = it }

        if (child.isEmpty()) return

        docRef.set(mapOf("reflectionV2" to child), SetOptions.merge()).await()
    }

}
