package com.mukmuk.todori.data.repository

import com.mukmuk.todori.data.remote.quest.DailyUserQuest
import com.mukmuk.todori.data.remote.quest.Quest
import com.mukmuk.todori.data.service.QuestService
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class QuestRepository @Inject constructor(
    private val questService: QuestService
) {
    /**
     * 오늘자 유저 퀘스트 가져오기
     * - 기존 퀘스트가 있으면 그대로 반환
     * - 없으면 조건에 맞는 퀘스트 중 랜덤 5개 배정
     */
    suspend fun getUserDailyQuests(uid: String): List<DailyUserQuest> {
        val existingQuests = questService.firestore.collection("users")
            .document(uid)
            .collection("dailyUserQuests")
            .get()
            .await()
            .toObjects(DailyUserQuest::class.java)

        if (existingQuests.isNotEmpty()) return existingQuests

        // 전체 퀘스트 불러오기
        val allQuests = questService.firestore.collection("quests")
            .get()
            .await()
            .toObjects(Quest::class.java)

        // 조건 기반 필터링
        val validQuests = allQuests.filter { quest ->
            when (quest.questId) {
                // 스터디 관련 → 스터디에 가입해야 가능
                "Q_STUDY_TODO_DONE", "Q_STUDY_3_TODO_DONE" ->
                    questService.isStudyMember(uid)

                // 연속 로그인 → 어제 로그인했어야 가능
                "Q_CONSISTENT_LOGIN_3", "Q_CONSISTENT_LOGIN_7" ->
                    questService.hasYesterdayLogin(uid)

                // 연속 타이머 → 어제 타이머 기록이 있어야 가능
                "Q_CONSISTENT_TIMER_3", "Q_CONSISTENT_TIMER_7" ->
                    questService.hasYesterdayTimer(uid)

                else -> true
            }
        }
            // 같은 그룹(로그인/타이머/회고)에서 더 긴 조건 우선
            .groupBy { quest ->
                when (quest.questId) {
                    "Q_CONSISTENT_LOGIN_3", "Q_CONSISTENT_LOGIN_7" -> "LOGIN"
                    "Q_CONSISTENT_TIMER_3", "Q_CONSISTENT_TIMER_7" -> "TIMER"
                    "Q_REFLECTION_WRITE_3", "Q_REFLECTION_WRITE_7" -> "REFLECTION"
                    else -> quest.questId
                }
            }
            .mapValues { (_, quests) ->
                quests.maxByOrNull { q ->
                    when (q.questId) {
                        "Q_CONSISTENT_LOGIN_7", "Q_CONSISTENT_TIMER_7", "Q_REFLECTION_WRITE_7" -> 7
                        "Q_CONSISTENT_LOGIN_3", "Q_CONSISTENT_TIMER_3", "Q_REFLECTION_WRITE_3" -> 3
                        else -> 1
                    }
                }!!
            }
            .values.toList()

        // 랜덤 5개 선택
        val randomQuests = validQuests.shuffled().take(5)

        // Firestore에 저장
        val userDailyRef = questService.firestore.collection("users")
            .document(uid)
            .collection("dailyUserQuests")

        val newUserQuests = randomQuests.map { quest ->
            DailyUserQuest(
                questId = quest.questId,
                title = quest.title,
                description = quest.description,
                completed = false
            ).also { uq ->
                userDailyRef.document(uq.questId).set(uq).await()
            }
        }

        return newUserQuests
    }

    /**
     * 오늘자 퀘스트 완료 여부 갱신
     */
    suspend fun updateUserDailyQuests(uid: String) {
        questService.updateDailyUserQuests(uid)
    }
}
