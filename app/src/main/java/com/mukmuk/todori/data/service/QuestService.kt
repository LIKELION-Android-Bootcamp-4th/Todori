package com.mukmuk.todori.data.service

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.mukmuk.todori.data.remote.quest.DailyUserQuest
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class QuestService @Inject constructor(
    val firestore: FirebaseFirestore
) {
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private fun today(): String = dateFormatter.format(Date())

    /** ------------------ 로그인 관련 ------------------ */
    suspend fun hasTodayLogin(uid: String): Boolean {
        val doc = firestore.collection("dailyRecords")
            .document("${uid}_${today()}")
            .get()
            .await()
        return doc.exists()
    }

    suspend fun hasYesterdayLogin(uid: String): Boolean {
        val cal = Calendar.getInstance().apply { add(Calendar.DATE, -1) }
        val yesterday = dateFormatter.format(cal.time)
        val doc = firestore.collection("dailyRecords")
            .document("${uid}_${yesterday}")
            .get()
            .await()
        return doc.exists()
    }

    suspend fun checkContinuousLogin(uid: String, consecutiveDays: Long): Boolean {
        val snapshot = firestore.collection("dailyRecords")
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

    /** ------------------ 타이머 관련 ------------------ */
    suspend fun totalTodayTimer(uid: String): Long {
        val todos = firestore.collection("todos")
            .whereEqualTo("uid", uid)
            .whereEqualTo("date", today())
            .get()
            .await()
        return todos.documents.sumOf { it.getLong("totalFocusTimeMillis") ?: 0L }
    }

    suspend fun hasYesterdayTimer(uid: String): Boolean {
        val cal = Calendar.getInstance().apply { add(Calendar.DATE, -1) }
        val yesterday = dateFormatter.format(cal.time)
        val todos = firestore.collection("todos")
            .whereEqualTo("uid", uid)
            .whereEqualTo("date", yesterday)
            .get()
            .await()
        return todos.documents.any { (it.getLong("totalFocusTimeMillis") ?: 0L) > 0 }
    }

    suspend fun checkContinuousTimerUsage(uid: String, consecutiveDays: Long): Boolean {
        val todos = firestore.collection("todos")
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

    /** ------------------ 회고 관련 ------------------ */
    suspend fun hasTodayReflection(uid: String): Boolean {
        val doc = firestore.collection("dailyRecords")
            .document("${uid}_${today()}")
            .get()
            .await()
        return doc.exists() && !doc.getString("reflection").isNullOrEmpty()
    }

    suspend fun checkContinuousReflection(uid: String, consecutiveDays: Long): Boolean {
        val snapshot = firestore.collection("dailyRecords")
            .whereEqualTo("uid", uid)
            .orderBy("date")
            .limitToLast(consecutiveDays)
            .get()
            .await()

        if (snapshot.size() < consecutiveDays) return false
        val records = snapshot.documents
        for (doc in records) {
            if (doc.getString("reflection").isNullOrEmpty()) return false
        }

        val dates = records.mapNotNull { it.getString("date") }
        for (i in 0 until dates.size - 1) {
            val prev = dateFormatter.parse(dates[i]) ?: return false
            val next = dateFormatter.parse(dates[i + 1]) ?: return false
            val diff = (next.time - prev.time) / (1000 * 60 * 60 * 24)
            if (diff != 1L) return false
        }
        return true
    }

    /** ------------------ Todo 관련 ------------------ */
    suspend fun hasAddedTodoToday(uid: String): Boolean {
        val snapshot = firestore.collection("todos")
            .whereEqualTo("uid", uid)
            .whereEqualTo("date", today())
            .get()
            .await()
        return snapshot.size() > 0
    }

    suspend fun hasCompletedTodoCount(uid: String, count: Int): Boolean {
        val snapshot = firestore.collection("todos")
            .whereEqualTo("uid", uid)
            .whereEqualTo("date", today())
            .whereEqualTo("completed", true)
            .get()
            .await()
        return snapshot.size() >= count
    }

    suspend fun hasGoalTodoCount(uid: String, count: Int): Boolean {
        val snapshot = firestore.collection("todos")
            .whereEqualTo("uid", uid)
            .whereEqualTo("date", today())
            .whereEqualTo("categoryId", "goal")
            .whereEqualTo("completed", true)
            .get()
            .await()
        return snapshot.size() >= count
    }

    /** ------------------ 스터디 관련 ------------------ */
    suspend fun hasStudyTodoDone(uid: String): Boolean {
        val studies = firestore.collection("studies")
            .whereEqualTo("status", "ACTIVE")
            .get()
            .await()

        for (study in studies.documents) {
            val todos = study.reference.collection("todos").get().await()
            for (todoDoc in todos.documents) {
                val progress = todoDoc.reference.collection("progress")
                    .document(uid).get().await()
                if (progress.exists() && progress.getBoolean("isDone") == true) {
                    return true
                }
            }
        }
        return false
    }

    suspend fun hasAllStudyTodoDone(uid: String): Boolean {
        val studies = firestore.collection("studies")
            .whereEqualTo("status", "ACTIVE")
            .get()
            .await()

        for (study in studies.documents) {
            val todos = study.reference.collection("todos").get().await()
            if (todos.isEmpty) continue

            val allDone = todos.documents.all { todoDoc ->
                val progress = todoDoc.reference.collection("progress")
                    .document(uid).get().await()
                progress.exists() && progress.getBoolean("isDone") == true
            }

            if (allDone) return true
        }
        return false
    }

    suspend fun isStudyMember(uid: String): Boolean {
        val studies = firestore.collection("studies")
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

    /** ------------------ 타이머 기록 관련 ------------------ */
    suspend fun countTodayTimerRecords(uid: String): Int {
        val todos = firestore.collection("todos")
            .whereEqualTo("uid", uid)
            .whereEqualTo("date", today())
            .get()
            .await()

        var count = 0
        for (todoDoc in todos.documents) {
            val sessions: QuerySnapshot = todoDoc.reference.collection("sessions").get().await()
            if (sessions.documents.isNotEmpty()) count++
        }
        return count
    }

    /** ------------------ 시간 조건 ------------------ */
    suspend fun hasMorningStudy(uid: String): Boolean {
        val todos = firestore.collection("todos")
            .whereEqualTo("uid", uid)
            .whereEqualTo("date", today())
            .get()
            .await()

        for (todoDoc in todos.documents) {
            val sessions = todoDoc.reference.collection("sessions").get().await()
            for (session in sessions.documents) {
                val startedAt = session.getTimestamp("startedAt") ?: continue
                val hour = Calendar.getInstance().apply { time = startedAt.toDate() }
                    .get(Calendar.HOUR_OF_DAY)
                if (hour < 9) return true
            }
        }
        return false
    }

    suspend fun hasLateNightStudy(uid: String): Boolean {
        val todos = firestore.collection("todos")
            .whereEqualTo("uid", uid)
            .whereEqualTo("date", today())
            .get()
            .await()

        for (todoDoc in todos.documents) {
            val sessions = todoDoc.reference.collection("sessions").get().await()
            for (session in sessions.documents) {
                val endedAt = session.getTimestamp("endedAt") ?: continue
                val hour = Calendar.getInstance().apply { time = endedAt.toDate() }
                    .get(Calendar.HOUR_OF_DAY)
                if (hour >= 23) return true
            }
        }
        return false
    }

    /** ------------------ 퀘스트 완료 여부 갱신 ------------------ */
    suspend fun updateDailyUserQuests(uid: String) {
        val userQuestsRef = firestore.collection("users")
            .document(uid)
            .collection("dailyUserQuests")

        val snapshot = userQuestsRef.get().await()
        val quests = snapshot.toObjects(DailyUserQuest::class.java)

        for (quest in quests) {
            val completed = when (quest.questId) {
                // 로그인 관련
                "Q_FIRST_LOGIN" -> hasTodayLogin(uid)
                "Q_CONSISTENT_LOGIN_3" -> checkContinuousLogin(uid, 3L)
                "Q_CONSISTENT_LOGIN_7" -> checkContinuousLogin(uid, 7L)

                // 타이머 관련
                "Q_TIMER_60" -> totalTodayTimer(uid) >= 60L * 60 * 1000
                "Q_TIMER_120" -> totalTodayTimer(uid) >= 120L * 60 * 1000
                "Q_TIMER_180" -> totalTodayTimer(uid) >= 180L * 60 * 1000
                "Q_CONSISTENT_TIMER_3" -> checkContinuousTimerUsage(uid, 3L)
                "Q_CONSISTENT_TIMER_7" -> checkContinuousTimerUsage(uid, 7L)

                // 회고 관련
                "Q_REFLECTION_WRITE" -> hasTodayReflection(uid)
                "Q_REFLECTION_WRITE_3" -> checkContinuousReflection(uid, 3L)
                "Q_REFLECTION_WRITE_7" -> checkContinuousReflection(uid, 7L)

                // Todo 관련
                "Q_TODO_ADD" -> hasAddedTodoToday(uid)
                "Q_TODO_COMPLETE" -> hasCompletedTodoCount(uid, 1)
                "Q_TODO_3_COMPLETE" -> hasCompletedTodoCount(uid, 3)
                "Q_GOAL_TODO_DONE" -> hasGoalTodoCount(uid, 1)
                "Q_GOAL_3_TODO_DONE" -> hasGoalTodoCount(uid, 3)

                // 스터디 관련
                "Q_STUDY_TODO_DONE" -> hasStudyTodoDone(uid)
                "Q_STUDY_3_TODO_DONE" -> hasAllStudyTodoDone(uid)

                // 타이머 기록 관련
                "Q.TIMER_RECORD" -> countTodayTimerRecords(uid) >= 1
                "Q.TIMER_RECORD_3" -> countTodayTimerRecords(uid) >= 3

                // 시간 조건
                "Q_MORNING_START" -> hasMorningStudy(uid)
                "Q_LATE_NIGHT_STUDY" -> hasLateNightStudy(uid)

                else -> quest.completed
            }

            if (completed && !quest.completed) {
                userQuestsRef.document(quest.questId)
                    .update("isCompleted", true)
                    .await()
            }
        }
    }
}
