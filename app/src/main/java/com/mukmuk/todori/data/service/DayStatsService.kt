package com.mukmuk.todori.data.service

import com.google.firebase.firestore.FirebaseFirestore
import com.mukmuk.todori.data.remote.stat.DayStat
import com.mukmuk.todori.data.remote.stat.Stats
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import javax.inject.Inject

class DayStatsService @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private fun userDoc(uid: String) = firestore.collection("users").document(uid)

    suspend fun getDayStat(uid: String, date: LocalDate): DayStat? {
        val dateStr = date.toString()
        val snap = userDoc(uid).collection("dayStats").document(dateStr).get().await()
        return snap.toObject(DayStat::class.java)
    }

    suspend fun getStats(uid: String): Stats? {
        val snap = userDoc(uid).collection("stats").document("streak").get().await()
        return snap.toObject(Stats::class.java)
    }
}
