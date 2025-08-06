package com.mukmuk.todori.data.service

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.mukmuk.todori.data.remote.quest.DailyUserQuest
import com.mukmuk.todori.data.remote.quest.Quest
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class QuestService(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private fun today(): String = dateFormatter.format(Date())

    /** 모든 퀘스트 가져오기 */
    suspend fun getAllQuests(): List<Quest> =
        db.collection("quests").get().await().toObjects(Quest::class.java)

    /** 오늘자 유저 퀘스트 가져오기 */
    suspend fun getUserDailyQuests(uid: String): List<DailyUserQuest> {
        val snapshot = db.collection("users")
            .document(uid)
            .collection("dailyUserQuests")
            .get()
            .await()
        return snapshot.toObjects(DailyUserQuest::class.java)
    }

    /** 퀘스트 조건 검사 후 갱신 */
    suspend fun updateDailyUserQuests(uid: String) {
        val quests = getAllQuests()

        for (quest in quests) {
            val isCompleted = when (quest.questId) {
                // 접속 관련
                "Q_FIRST_LOGIN" -> hasTodayLogin(uid)
                "Q_CONSISTENT_LOGIN_3" -> checkContinuousLogin(uid, 3L)
                "Q_CONSISTENT_LOGIN_7" -> checkContinuousLogin(uid, 7L)

                // 타이머 관련
                "Q_TIMER_60" -> totalTodayTimer(uid) >= 60 * 60 * 1000
                "Q_TIMER_120" -> totalTodayTimer(uid) >= 120 * 60 * 1000
                "Q_TIMER_180" -> totalTodayTimer(uid) >= 180 * 60 * 1000
                "Q_CONSISTENT_TIMER_3" -> checkContinuousTimerUsage(uid, 3L)
                "Q_CONSISTENT_TIMER_7" -> checkContinuousTimerUsage(uid, 7L)

                // Todo 관련
                "Q_TODO_ADD" -> hasAddedTodoToday(uid)
                "Q_TODO_COMPLETE" -> hasCompletedTodoCount(uid, 1)
                "Q_TODO_3_COMPLETE" -> hasCompletedTodoCount(uid, 3)
                "Q_GOAL_TODO_DONE" -> hasGoalTodoCount(uid, 1)
                "Q_GOAL_3_TODO_DONE" -> hasGoalTodoCount(uid, 3)

                // 스터디 관련
                "Q_STUDY_TODO_DONE" -> hasStudyTodoDone(uid)
                "Q_STUDY_3_TODO_DONE" -> hasAllStudyTodoDone(uid)

                // 회고 관련
                "Q_REFLECTION_WRITE" -> hasTodayReflection(uid)
                "Q_REFLECTION_WRITE_3" -> checkContinuousReflection(uid, 3L)
                "Q_REFLECTION_WRITE_7" -> checkContinuousReflection(uid, 7L)

                // 타이머 기록 관련
                "Q.TIMER_RECORD" -> countTodayTimerRecords(uid) >= 1
                "Q.TIMER_RECORD_3" -> countTodayTimerRecords(uid) >= 3

                // 시간 조건
                "Q_MORNING_START" -> hasMorningStudy(uid)
                "Q_LATE_NIGHT_STUDY" -> hasLateNightStudy(uid)

                else -> false
            }

            val ref = db.collection("users")
                .document(uid)
                .collection("dailyUserQuests")
                .document(quest.questId)

            ref.set(
                mapOf(
                    "questId" to quest.questId,
                    "title" to quest.title,
                    "description" to quest.description,
                    "isCompleted" to isCompleted,
                    "updatedAt" to Timestamp.now()
                )
            ).await()
        }
    }

    // ------------------ 개별 조건 함수 ------------------

    /** 오늘 로그인 했는지 */
    private suspend fun hasTodayLogin(uid: String): Boolean {
        val doc = db.collection("dailyRecords")
            .document("${uid}_${today()}")
            .get()
            .await()
        return doc.exists()
    }

    /** N일 연속 로그인 */
    private suspend fun checkContinuousLogin(uid: String, consecutiveDays: Long): Boolean {
        val snapshot = db.collection("dailyRecords")
            .whereEqualTo("uid", uid)
            .orderBy("date")
            .limitToLast(consecutiveDays)
            .get()
            .await()

        if (snapshot.size() < consecutiveDays) return false
        val dates = snapshot.documents.mapNotNull { it.getString("date") }
        if (dates.size < consecutiveDays) return false

        for (i in 0 until dates.size - 1) {
            val prev = dateFormatter.parse(dates[i]) ?: return false
            val next = dateFormatter.parse(dates[i + 1]) ?: return false
            val diff = (next.time - prev.time) / (1000 * 60 * 60 * 24)
            if (diff != 1L) return false
        }
        return true
    }

    /** 오늘 총 타이머 사용량(ms) */
    private suspend fun totalTodayTimer(uid: String): Long {
        val todos = db.collection("todos")
            .whereEqualTo("uid", uid)
            .whereEqualTo("date", today())
            .get()
            .await()

        return todos.documents.sumOf { it.getLong("totalFocusTimeMillis") ?: 0L }
    }

    /** N일 연속 매일 3시간 이상 공부 */
    private suspend fun checkContinuousTimerUsage(uid: String, consecutiveDays: Long): Boolean {
        val records = mutableListOf<String>()

        val todos = db.collection("todos")
            .whereEqualTo("uid", uid)
            .orderBy("date")
            .limitToLast(consecutiveDays)
            .get()
            .await()

        val todosByDate = todos.documents.groupBy { it.getString("date") ?: "" }
        if (todosByDate.size < consecutiveDays) return false

        for ((date, dayTodos) in todosByDate) {
            val totalTime = dayTodos.sumOf { it.getLong("totalFocusTimeMillis") ?: 0L }
            if (totalTime < 3 * 60 * 60 * 1000) return false
            records.add(date)
        }

        // 날짜 연속성 체크
        val dates = records.sorted()
        for (i in 0 until dates.size - 1) {
            val prev = dateFormatter.parse(dates[i]) ?: return false
            val next = dateFormatter.parse(dates[i + 1]) ?: return false
            val diff = (next.time - prev.time) / (1000 * 60 * 60 * 24)
            if (diff != 1L) return false
        }
        return true
    }

    /** 오늘 Todo 추가 여부 */
    private suspend fun hasAddedTodoToday(uid: String): Boolean {
        val snapshot = db.collection("todos")
            .whereEqualTo("uid", uid)
            .whereEqualTo("date", today())
            .get()
            .await()
        return snapshot.size() > 0
    }

    /** 오늘 완료된 Todo 개수 */
    private suspend fun hasCompletedTodoCount(uid: String, count: Int): Boolean {
        val snapshot = db.collection("todos")
            .whereEqualTo("uid", uid)
            .whereEqualTo("date", today())
            .whereEqualTo("completed", true)
            .get()
            .await()
        return snapshot.size() >= count
    }

    /** 오늘 목표 Todo 완료 개수 */
    private suspend fun hasGoalTodoCount(uid: String, count: Int): Boolean {
        val snapshot = db.collection("todos")
            .whereEqualTo("uid", uid)
            .whereEqualTo("date", today())
            .whereEqualTo("categoryId", "goal") // 목표 Todo 카테고리 예시
            .whereEqualTo("completed", true)
            .get()
            .await()
        return snapshot.size() >= count
    }

    /** 스터디 Todo 1개 완료 */
    private suspend fun hasStudyTodoDone(uid: String): Boolean {
        val studies = db.collection("studies")
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

    /** 스터디 Todo 모두 완료 */
    private suspend fun hasAllStudyTodoDone(uid: String): Boolean {
        val studies = db.collection("studies")
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

    /** 오늘 회고 작성 */
    private suspend fun hasTodayReflection(uid: String): Boolean {
        val doc = db.collection("dailyRecords")
            .document("${uid}_${today()}")
            .get()
            .await()
        return doc.exists() && !doc.getString("reflection").isNullOrEmpty()
    }

    /** N일 연속 회고 작성 */
    private suspend fun checkContinuousReflection(uid: String, consecutiveDays: Long): Boolean {
        val snapshot = db.collection("dailyRecords")
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

    /** 오늘 타이머 기록 Todo 개수 */
    private suspend fun countTodayTimerRecords(uid: String): Int {
        val todos = db.collection("todos")
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

    /** 오전 9시 전에 타이머 시작 */
    private suspend fun hasMorningStudy(uid: String): Boolean {
        val todos = db.collection("todos")
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

    /** 밤 11시 이후 타이머 종료 */
    private suspend fun hasLateNightStudy(uid: String): Boolean {
        val todos = db.collection("todos")
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
}
