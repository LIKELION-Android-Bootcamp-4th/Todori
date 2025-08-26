package com.mukmuk.todori.data.service

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mukmuk.todori.data.remote.quest.AssignedQuestDTO
import com.mukmuk.todori.data.remote.quest.MetaDTO
import com.mukmuk.todori.data.remote.quest.ProfileDTO
import com.mukmuk.todori.data.remote.quest.QuestFunctionResponse
import com.mukmuk.todori.data.remote.quest.RewardDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class QuestService @Inject constructor(
    val firestore: FirebaseFirestore
) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(7, TimeUnit.SECONDS)
        .readTimeout(12, TimeUnit.SECONDS)
        .writeTimeout(7, TimeUnit.SECONDS)
        .build()

    private val functionUrl = "https://updateuserquest-qbko4v5l2q-uc.a.run.app"

    private suspend fun getIdToken(): String? {
        val user = FirebaseAuth.getInstance().currentUser ?: return null
        return try {
            user.getIdToken(false).await().token
        } catch (e: Exception) {
            Log.e("QuestService", "getIdToken failed", e)
            null
        }

    }

    suspend fun callQuest(uid: String): Result<QuestFunctionResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val idToken = getIdToken()
                if (idToken.isNullOrBlank()) {
                    val msg = "Missing Firebase ID token"
                    Log.e("QuestService", msg)
                    return@withContext Result.failure(IllegalStateException(msg))
                }

                val payload = JSONObject().apply { put("uid", uid) }
                val reqBody = payload.toString().toRequestBody("application/json".toMediaType())

                val req = Request.Builder()
                    .url(functionUrl)
                    .post(reqBody)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer $idToken")
                    .build()

                client.newCall(req).execute().use { resp ->
                    val body = resp.body?.string()
                    if (!resp.isSuccessful || body.isNullOrBlank()) {
                        val msg = "HTTP ${resp.code} / body=${body?.take(200)}"
                        return@use Result.failure(RuntimeException(msg))
                    }
                    val json = JSONObject(body)

                    val assignedJson = json.getJSONArray("assigned")
                    val assigned = buildList {
                        for (i in 0 until assignedJson.length()) {
                            val o = assignedJson.getJSONObject(i)
                            add(
                                AssignedQuestDTO(
                                    questId = o.getString("questId"),
                                    title = o.optString("title"),
                                    completed = o.optBoolean("completed", false),
                                    points = o.optInt("points", 0)
                                )
                            )
                        }
                    }

                    val rewardObj = json.optJSONObject("reward")
                    val profileObj = json.optJSONObject("profile")
                    val metaObj = json.optJSONObject("meta")

                    val result = QuestFunctionResponse(
                        assigned = assigned,
                        reward = RewardDTO(
                            gainedPoint = rewardObj?.optInt("gainedPoint") ?: 0,
                            levelUp = rewardObj?.optBoolean("levelUp") ?: false,
                            newLevel = rewardObj?.optInt("newLevel") ?: (profileObj?.optInt("level") ?: 1)
                        ),
                        profile = ProfileDTO(
                            level = profileObj?.optInt("level") ?: 1,
                            // rewardPoint = 현 레벨  진행도
                            rewardPoint = profileObj?.optInt("rewardPoint") ?: 0,
                            // nextLevelPoint = 다음 레벨까지 남은 포인트
                            nextLevelPoint = profileObj?.optInt("nextLevelPoint") ?: 0
                        ),
                        meta = MetaDTO(
                            today = metaObj?.optString("today"),
                            joinedStudy = metaObj?.optBoolean("joinedStudy")
                        )
                    )
                    Result.success(result)
                }
            } catch (e: Exception) {
                Log.e("QuestService", "❌ HTTP/파싱 실패", e)
                Result.failure(e)
            }
        }
    }
}
