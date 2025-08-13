package com.mukmuk.todori.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.mukmuk.todori.data.remote.quest.DailyUserQuest
import com.mukmuk.todori.data.service.QuestFunctionResponse
import com.mukmuk.todori.data.service.QuestService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class QuestRepository @Inject constructor(
    private val questService: QuestService,
    private val firestore: FirebaseFirestore
) {
    /** 서버 1회 호출(배정+판정+보상) */
    suspend fun callUserQuest(uid: String): Result<QuestFunctionResponse> =
        questService.callQuest(uid)

    /** 폴백용 캐시 조회 */
    suspend fun getCachedDailyQuests(uid: String): List<DailyUserQuest> =
        withContext(Dispatchers.IO) {
            firestore.collection("users")
                .document(uid)
                .collection("dailyUserQuests")
                .get()
                .await()
                .toObjects(DailyUserQuest::class.java)
        }
}
