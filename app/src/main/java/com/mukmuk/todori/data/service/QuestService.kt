package com.mukmuk.todori.data.service

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.mukmuk.todori.data.remote.quest.AssignedQuestDTO
import com.mukmuk.todori.data.remote.quest.MetaDTO
import com.mukmuk.todori.data.remote.quest.ProfileDTO
import com.mukmuk.todori.data.remote.quest.QuestFunctionResponse
import com.mukmuk.todori.data.remote.quest.RewardDTO
import kotlinx.coroutines.Dispatchers
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

    // Cloud Run URL (배포 로그에서 실제 URL 확인)
    private val functionUrl = "https://updateuserquest-qbko4v5l2q-uc.a.run.app"

    /** 서버에서 오늘 퀘스트 배정+판정+보상 수행 → 강타입 DTO 반환 */
    suspend fun callQuest(uid: String): Result<QuestFunctionResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val payload = JSONObject().apply { put("uid", uid) }
                val reqBody = payload.toString().toRequestBody("application/json".toMediaType())

                val req = Request.Builder()
                    .url(functionUrl)
                    .post(reqBody)
                    .addHeader("Content-Type", "application/json")
                    .build()

                client.newCall(req).execute().use { resp ->
                    val body = resp.body?.string()
                    if (!resp.isSuccessful || body.isNullOrBlank()) {
                        val msg = "HTTP ${resp.code} / body=${body?.take(200)}"
                        Log.e("QuestService", "❌ Functions 실패: $msg")
                        return@use Result.failure(RuntimeException(msg))
                    }
                    Log.d("QuestService", "✅ Functions 응답: ${body.take(400)}")
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
                            newLevel = rewardObj?.optInt("newLevel") ?: (profileObj?.optInt("level")
                                ?: 1)
                        ),
                        profile = ProfileDTO(
                            level = profileObj?.optInt("level") ?: 1,
                            // 서버 의미: rewardPoint = 현 레벨 버킷 진행도
                            rewardPoint = profileObj?.optInt("rewardPoint") ?: 0,
                            // 서버 의미: nextLevelPoint = 다음 레벨까지 남은 포인트
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


