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

class HomeService(private val firestore: FirebaseFirestore) {
    private fun dailyRecordRef(uid: String) =
        firestore.collection("users").document(uid).collection("dailyRecord")

    @RequiresApi(Build.VERSION_CODES.O)
    private fun formatDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return date.format(formatter)
    }

    suspend fun createDailyRecord(uid: String, dailyRecord: DailyRecord) {
        val ref = dailyRecordRef(uid).document(dailyRecord.date)
        ref.set(dailyRecord).await()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getDailyRecord(uid: String, date: LocalDate): List<DailyRecord> {
        val formattedDate = formatDate(date)
        val snapshot: QuerySnapshot = dailyRecordRef(uid)
            .whereEqualTo("date", formattedDate)
            .get()
            .await()
        return snapshot.documents.mapNotNull { it.toObject(DailyRecord::class.java) }
    }

    suspend fun updateDailyRecord(uid: String, dailyRecord: DailyRecord) {
        dailyRecordRef(uid).document(dailyRecord.date)
            .set(dailyRecord, SetOptions.merge())
            .await()
    }
}