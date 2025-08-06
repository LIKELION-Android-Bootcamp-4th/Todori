package com.mukmuk.todori.data.service

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class QuestService(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun getAllQuests() =
        db.collection("quest").get().await().documents
    //db.collection("quests").get().await().documents


    suspend fun getTodayQuests(uid: String, today: String) =
        db.collection("users").document(uid)
            .collection("dailyUserQuests")
            .whereEqualTo("date", today)
            .get()
            .await()

    suspend fun saveDailyQuest(uid: String, questId: String, data: Map<String, Any>) {
        db.collection("users").document(uid)
            .collection("dailyUserQuests")
            .document(questId)
            .set(data)
            .await()
    }
}