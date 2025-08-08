package com.mukmuk.todori.data.service

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.mukmuk.todori.data.remote.dailyRecord.DailyRecord
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DailyRecordService(
    private val firestore: FirebaseFirestore
) {
    // users/{uid}/dailyRecord 컬렉션 참조
    private fun userDailyRecordRef(uid: String) =
        firestore.collection("users").document(uid).collection("dailyRecord")

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