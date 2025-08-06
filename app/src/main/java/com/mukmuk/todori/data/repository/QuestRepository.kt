package com.mukmuk.todori.data.repository

import com.google.firebase.Timestamp
import com.mukmuk.todori.data.remote.quest.DailyUserQuest
import com.mukmuk.todori.data.remote.quest.Quest
import com.mukmuk.todori.data.service.QuestService
import kotlinx.coroutines.tasks.await

class QuestRepository(
    private val questService: QuestService
) {
    /** 오늘자 유저 퀘스트 가져오기 (없으면 조건에 맞는 랜덤 5개 배정) */
    suspend fun getUserDailyQuests(uid: String): List<DailyUserQuest> {
        val existingQuests = questService.db.collection("users")
            .document(uid)
            .collection("dailyUserQuests")
            .get()
            .await()
            .toObjects(DailyUserQuest::class.java)

        if (existingQuests.isNotEmpty()) return existingQuests

        // 전체 퀘스트 가져오기
        val allQuests = questService.db.collection("quests")
            .get()
            .await()
            .toObjects(Quest::class.java)

        // 그룹별 최댓값만 남기고, 사용자 조건에 맞는 것만 필터링
        val validQuests = allQuests.filter { quest ->
            when (quest.questId) {
                "Q_STUDY_TODO_DONE", "Q_STUDY_3_TODO_DONE" ->
                    questService.isStudyMember(uid)

                "Q_CONSISTENT_LOGIN_3", "Q_CONSISTENT_LOGIN_7" ->
                    questService.hasYesterdayLogin(uid)

                "Q_CONSISTENT_TIMER_3", "Q_CONSISTENT_TIMER_7" ->
                    questService.hasYesterdayTimer(uid)

                else -> true
            }
        }.groupBy { quest ->
            when (quest.questId) {
                "Q_CONSISTENT_LOGIN_3", "Q_CONSISTENT_LOGIN_7" -> "LOGIN"
                "Q_CONSISTENT_TIMER_3", "Q_CONSISTENT_TIMER_7" -> "TIMER"
                "Q_REFLECTION_WRITE_3", "Q_REFLECTION_WRITE_7" -> "REFLECTION"
                else -> quest.questId
            }
        }.mapValues { (_, quests) ->
            // 그룹 내에서 더 긴 연속 조건을 우선
            quests.maxByOrNull { q ->
                when (q.questId) {
                    "Q_CONSISTENT_LOGIN_7", "Q_CONSISTENT_TIMER_7", "Q_REFLECTION_WRITE_7" -> 7
                    "Q_CONSISTENT_LOGIN_3", "Q_CONSISTENT_TIMER_3", "Q_REFLECTION_WRITE_3" -> 3
                    else -> 1
                }
            }!!
        }.values.toList()

        // 랜덤 5개 선택
        val randomQuests = validQuests.shuffled().take(5)

        // Firestore에 저장
        val userDailyRef = questService.db.collection("users")
            .document(uid)
            .collection("dailyUserQuests")

        val newUserQuests = randomQuests.map { quest ->
            DailyUserQuest(
                questId = quest.questId,
                title = quest.title,
                description = quest.description,
                completed = false,
            ).also { uq ->
                userDailyRef.document(uq.questId).set(uq).await()
            }
        }

        return newUserQuests
    }
}
