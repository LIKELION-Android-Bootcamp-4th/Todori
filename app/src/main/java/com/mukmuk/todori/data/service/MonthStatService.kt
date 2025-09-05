package com.mukmuk.todori.data.service

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.FirebaseFirestore
import com.mukmuk.todori.data.remote.stat.MonthStat
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
class MonthStatService @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private fun monthStatDoc(uid: String, monthKey: String) =
        firestore.collection("users").document(uid)
            .collection("monthStats").document(monthKey)

    suspend fun getMonthStat(uid: String, year: Int, month: Int): MonthStat? {
        val monthKey = "%04d-%02d".format(year, month)
        val snapshot = monthStatDoc(uid, monthKey).get().await()
        return snapshot.toObject(MonthStat::class.java)
    }
}