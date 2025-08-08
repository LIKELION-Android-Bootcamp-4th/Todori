package com.mukmuk.todori.data.service

import android.content.Context
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.functions.FirebaseFunctions
import com.kakao.sdk.user.UserApiClient
import com.mukmuk.todori.data.remote.user.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


class UserService @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private fun userDoc(uid: String) =
        firestore.collection("users").document(uid)

    //프로필 조회
    suspend fun getProfile(uid: String): User? {
        val snapshot = userDoc(uid).get().await()
        return snapshot.toObject(User::class.java)
    }


    //프로필 수정
    suspend fun updateUser(uid: String, nickname: String, intro: String) {
        userDoc(uid).set(
            mapOf("nickname" to nickname, "intro" to intro),
            SetOptions.merge()
        )
            .await()
    }

    //마지막 로그인 시간 업데이트
    suspend fun updateLastLogin(uid: String) {
        userDoc(uid).set(
            mapOf("lastLoginAt" to Timestamp.now()),
            SetOptions.merge()
        ).await()
    }

    /**
     * 카카오 로그인 → Cloud Functions(kakaoCustomAuth) → Firebase 로그인
     */
    suspend fun kakaoLogin(context: Context) {
        try {
            val accessToken = getKakaoAccessToken(context)

            val functions = FirebaseFunctions.getInstance()
            val auth = FirebaseAuth.getInstance()

            // 1) 함수 호출 결과를 await()로 받아서
            val result = functions
                .getHttpsCallable("kakaoCustomAuth")
                .call(mapOf("accessToken" to accessToken))
                .await()

            val data = result.data as? Map<*, *>
                ?: throw IllegalStateException("Invalid response from kakaoCustomAuth")
            val customToken = data["token"] as? String
                ?: throw IllegalStateException("No token in kakaoCustomAuth response")

            auth.signInWithCustomToken(customToken).await()

            Log.d("KAKAO", "Firebase 로그인 성공: ${auth.currentUser?.uid}")
        } catch (e: Exception) {
            Log.e("KAKAO", "카카오 로그인 실패", e)
            throw e
        }
    }


    /**
     * 카카오 SDK를 통해 accessToken 가져오기
     */
    private suspend fun getKakaoAccessToken(context: Context): String {
        return suspendCoroutine { cont ->
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
                UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                    if (error != null) cont.resumeWithException(error)
                    else if (token != null) cont.resume(token.accessToken)
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(context) { token, error ->
                    if (error != null) cont.resumeWithException(error)
                    else if (token != null) cont.resume(token.accessToken)
                }
            }
        }
    }




}