package com.mukmuk.todori.data.service

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.mukmuk.todori.data.remote.dailyRecord.DailyRecord
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DailyRecordService @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    // users/{uid}/dailyRecord 컬렉션 참조
    private fun userDailyRecordRef(uid: String) =
        firestore.collection("users").document(uid).collection("dailyRecord")

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
    // 선택 날짜 dailyRecord 불러오기
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getDailyRecordByDate(uid: String, date: LocalDate): List<DailyRecord> {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedDate = date.format(formatter)
        val snapshot: QuerySnapshot = userDailyRecordRef(uid)
            .whereEqualTo("date", formattedDate)
            .get()
            .await()
        return snapshot.documents.mapNotNull { it.toObject(DailyRecord::class.java) }
    }

    suspend fun updateDailyRecord(uid: String, record: DailyRecord) {
        val documentId = record.date
        userDailyRecordRef(uid).document(documentId)
            .set(record, SetOptions.merge())
            .await()
    }
}
