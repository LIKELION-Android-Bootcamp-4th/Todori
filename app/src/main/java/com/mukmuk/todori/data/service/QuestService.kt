package com.mukmuk.todori.data.service

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class QuestService @Inject constructor(
    val firestore: FirebaseFirestore,
    private val functions: FirebaseFunctions
) {
    suspend fun callQuestCheckFunction(uid: String): Result<String> {
        return try {
            val result = functions
                .getHttpsCallable("checkUserQuests")
                .call(mapOf("uid" to uid))
                .await()

            val message = result.data.toString()
            Result.success(message)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
