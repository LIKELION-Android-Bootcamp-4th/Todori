package com.mukmuk.todori.data.service

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class QuestService(
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private fun today(): String = dateFormatter.format(Date())

    /** 오늘 로그인 기록 여부 */
    suspend fun hasTodayLogin(uid: String): Boolean {
        val doc = db.collection("dailyRecords")
            .document("${uid}_${today()}")
            .get()
            .await()
        return doc.exists()
    }

    /** 어제 로그인 기록 여부 */
    suspend fun hasYesterdayLogin(uid: String): Boolean {
        val cal = Calendar.getInstance().apply { add(Calendar.DATE, -1) }
        val yesterday = dateFormatter.format(cal.time)

        val doc = db.collection("dailyRecords")
            .document("${uid}_${yesterday}")
            .get()
            .await()
        return doc.exists()
    }

    /** 최근 N일 연속 로그인 여부 */
    suspend fun checkContinuousLogin(uid: String, consecutiveDays: Long): Boolean {
        val snapshot = db.collection("dailyRecords")
            .whereEqualTo("uid", uid)
            .orderBy("date")
            .limitToLast(consecutiveDays)
            .get()
            .await()

        if (snapshot.size() < consecutiveDays) return false
        val dates = snapshot.documents.mapNotNull { it.getString("date") }

        for (i in 0 until dates.size - 1) {
            val prev = dateFormatter.parse(dates[i]) ?: return false
            val next = dateFormatter.parse(dates[i + 1]) ?: return false
            val diff = (next.time - prev.time) / (1000 * 60 * 60 * 24)
            if (diff != 1L) return false
        }
        return true
    }

    /** 오늘 총 공부 시간(ms) */
    suspend fun totalTodayTimer(uid: String): Long {
        val todos = db.collection("todos")
            .whereEqualTo("uid", uid)
            .whereEqualTo("date", today())
            .get()
            .await()
        return todos.documents.sumOf { it.getLong("totalFocusTimeMillis") ?: 0L }
    }

    /** 어제 타이머 기록 여부 */
    suspend fun hasYesterdayTimer(uid: String): Boolean {
        val cal = Calendar.getInstance().apply { add(Calendar.DATE, -1) }
        val yesterday = dateFormatter.format(cal.time)

        val todos = db.collection("todos")
            .whereEqualTo("uid", uid)
            .whereEqualTo("date", yesterday)
            .get()
            .await()

        return todos.documents.any { (it.getLong("totalFocusTimeMillis") ?: 0L) > 0 }
    }

    /** 최근 N일 연속 매일 3시간 이상 공부 여부 */
    suspend fun checkContinuousTimerUsage(uid: String, consecutiveDays: Long): Boolean {
        val todos = db.collection("todos")
            .whereEqualTo("uid", uid)
            .orderBy("date")
            .limitToLast(consecutiveDays)
            .get()
            .await()

        val grouped = todos.documents.groupBy { it.getString("date") ?: "" }
        if (grouped.size < consecutiveDays) return false

        val dates = grouped.keys.sorted()
        for (date in dates) {
            val totalTime = grouped[date]?.sumOf { it.getLong("totalFocusTimeMillis") ?: 0L } ?: 0L
            if (totalTime < 3L * 60 * 60 * 1000) return false
        }

        for (i in 0 until dates.size - 1) {
            val prev = dateFormatter.parse(dates[i]) ?: return false
            val next = dateFormatter.parse(dates[i + 1]) ?: return false
            val diff = (next.time - prev.time) / (1000 * 60 * 60 * 24)
            if (diff != 1L) return false
        }
        return true
    }

    /** 스터디 가입 여부 */
    suspend fun isStudyMember(uid: String): Boolean {
        val studies = db.collection("studies")
            .whereEqualTo("isDeleted", false)
            .get()
            .await()

        for (studyDoc in studies.documents) {
            val memberDoc = studyDoc.reference
                .collection("members")
                .document(uid)
                .get()
                .await()
            if (memberDoc.exists()) return true
        }
        return false
    }
}
