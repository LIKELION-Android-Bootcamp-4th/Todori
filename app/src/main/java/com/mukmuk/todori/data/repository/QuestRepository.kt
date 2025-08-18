package com.mukmuk.todori.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.mukmuk.todori.data.remote.quest.DailyUserQuest
import com.mukmuk.todori.data.service.QuestService
import com.mukmuk.todori.data.remote.quest.QuestFunctionResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class QuestRepository @Inject constructor(
    private val questService: QuestService,
    private val firestore: FirebaseFirestore
) {
    suspend fun getCachedDailyQuests(uid: String): List<DailyUserQuest> =
        firestore.collection("users").document(uid).collection("dailyUserQuests")
            .get().await().toObjects(DailyUserQuest::class.java)

    suspend fun refreshFromServer(uid: String) = questService.callQuest(uid)
}