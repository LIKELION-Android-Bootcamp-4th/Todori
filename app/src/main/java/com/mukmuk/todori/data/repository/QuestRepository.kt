package com.mukmuk.todori.data.repository

import com.mukmuk.todori.data.remote.quest.DailyUserQuest
import com.mukmuk.todori.data.remote.quest.Quest
import com.mukmuk.todori.data.service.QuestService
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class QuestRepository @Inject constructor(
    private val questService: QuestService
) {
    //유저 일일 퀘스트 불러오기
    suspend fun getUserDailyQuests(uid: String): List<DailyUserQuest> {
        val existingQuests = questService.firestore.collection("users")
            .document(uid)
            .collection("dailyUserQuests")
            .get()
            .await()
            .toObjects(DailyUserQuest::class.java)

        if (existingQuests.isNotEmpty()) return existingQuests

        val allQuests = questService.firestore.collection("quests")
            .get()
            .await()
            .toObjects(Quest::class.java)

        // 랜덤 5개 선택
        val randomQuests = allQuests.shuffled().take(5)

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

    suspend fun callQuestCheckFunction(uid: String): Result<String> {
        // 디버깅용 로그 추가
        println("🔥 callQuestCheckFunction 호출됨 - uid: '$uid'")  // 콘솔에 uid 값 찍기

        // uid가 null이거나 빈 문자열일 경우 경고
        if (uid.isBlank()) {
            println("⚠️ uid가 비어있습니다.")
        }

        return questService.callQuestCheckFunction(uid)
    }

    // 오늘자 퀘스트 완료 여부 갱신 -cloud function
    suspend fun updateUserDailyQuests(uid: String) {
        questService.callQuestCheckFunction(uid)
    }
}
