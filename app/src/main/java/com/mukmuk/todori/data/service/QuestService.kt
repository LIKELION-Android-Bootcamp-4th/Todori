package com.mukmuk.todori.data.service

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import javax.inject.Inject

class QuestService @Inject constructor(
    val firestore: FirebaseFirestore
) {
    private val client = OkHttpClient()

    private val functionUrl = "https://updateuserquest-qbko4v5l2q-uc.a.run.app"

    suspend fun callQuestCheckFunction(uid: String): Result<String> {
        println("📦 QuestService.callQuestCheckFunction - uid 전달: '$uid'")

        return withContext(Dispatchers.IO) {
            try {
                val data = JSONObject().apply {
                    put("uid", uid)
                }

                val requestBody = data.toString().toRequestBody("application/json".toMediaType())

                val request = Request.Builder()
                    .url(functionUrl)
                    .post(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                Log.d("QuestService", "✅ HTTP 호출 응답: $responseBody")

                Result.success(responseBody ?: "성공")
            } catch (e: Exception) {
                Log.e("QuestService", "❌ HTTP 호출 실패", e)
                Result.failure(e)
            }
        }
    }
}
