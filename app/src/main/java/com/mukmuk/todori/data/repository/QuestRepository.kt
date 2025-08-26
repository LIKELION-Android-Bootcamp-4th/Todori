package com.mukmuk.todori.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.mukmuk.todori.data.remote.quest.DailyUserQuest
import com.mukmuk.todori.data.service.QuestService
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class QuestRepository @Inject constructor(
    private val questService: QuestService,
    private val firestore: FirebaseFirestore
) {
    private val zoneKst = ZoneId.of("Asia/Seoul")
    private val dateFmt = DateTimeFormatter.ISO_LOCAL_DATE

    private fun todayKst(): String =
        LocalDate.now(zoneKst).format(dateFmt)

    suspend fun getCachedDailyQuests(uid: String): List<DailyUserQuest> {
        val today = todayKst()
        val snap = firestore.collection("users").document(uid)
            .collection("dailyUserQuests")
            .whereEqualTo("date", today)
            .whereEqualTo("archived", false)
            .orderBy("assignedAt")
            .get()
            .await()

        return snap.toObjects(DailyUserQuest::class.java)
    }

    suspend fun refreshFromServer(uid: String) = questService.callQuest(uid)
}
