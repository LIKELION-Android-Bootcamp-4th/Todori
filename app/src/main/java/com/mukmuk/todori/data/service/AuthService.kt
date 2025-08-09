package com.mukmuk.todori.data.service

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.firestore.FirebaseFirestore
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AuthService @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore,
    @ApplicationContext private val appContext: Context
) {
    suspend fun logout(provider: String?) {
        when (provider) {
            "google.com" -> googleSignOut()
            "kakao" -> kakaoLogout()
            "naver" -> naverLogout()
            else -> { /* 추후 추가되면? */ }
        }
        firebaseAuth.signOut()
        // TODO: Datastore/ROOM 데이터 추후 clear
    }

    private suspend fun kakaoLogout() = suspendCancellableCoroutine<Unit> { cont ->
        UserApiClient.instance.logout { error ->
            if (error != null) cont.resumeWithException(error) else cont.resume(Unit)
        }
    }

    private fun naverLogout() {
        NaverIdLoginSDK.logout()
    }

    private suspend fun googleSignOut() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        val client = GoogleSignIn.getClient(appContext, gso)
        client.signOut().await()
    }


    /**
     * 회원 탈퇴:
     * 1) provider 서비스 연동 해제(동의 철회)
     * 2) Firestore soft delete (deleted = true, fcmToken = null 등)
     * 3) Firebase Auth 계정 삭제
     * 4) 로컬 세션/캐시 정리
     */
    suspend fun deleteAccount(uid: String, authProvider: String?) {
        // 1) 연동 해제/동의 철회
        when (authProvider) {
            "google.com" -> googleRevokeAccess() // 탈퇴 시에는 revokeAccess 권장
            "kakao"      -> kakaoUnlink()
            "naver"      -> naverRevokeToken()
            else         -> { /* no-op */ }
        }

        // 2) Firestore soft delete
        runCatching {
            db.collection("users").document(uid).update(
                mapOf(
                    "deleted" to true,
                    "fcmToken" to null
                )
            )
        }

        // 3) Firebase Auth 계정 삭제 (최근 로그인 필요 시 예외)
        try {
            firebaseAuth.currentUser?.delete()
        } catch (e: FirebaseAuthRecentLoginRequiredException) {
            // 화면에서 "다시 로그인 후 탈퇴를 재시도하세요" 안내 필요
            throw e
        }

        // 4) 세션 정리
        runCatching { firebaseAuth.signOut() }
    }

    // ===== Provider helpers =====

    private suspend fun kakaoUnlink() = suspendCancellableCoroutine<Unit> { cont ->
        // 카카오 계정 연결 해제(동의 철회)
        UserApiClient.instance.unlink { error ->
            if (error != null) cont.resumeWithException(error) else cont.resume(Unit)
        }
    }

    private fun naverRevokeToken() {
        // 최신 시그니처: context 없이 callback만
        NidOAuthLogin().callDeleteTokenApi(object : OAuthLoginCallback {
            override fun onSuccess() { /* no-op */ }
            override fun onFailure(httpStatus: Int, message: String) { /* 로그원하면 여기에 */ }
            override fun onError(errorCode: Int, message: String) { onFailure(errorCode, message) }
        })
        // 덤으로 로그아웃도
        NaverIdLoginSDK.logout()
    }

    private suspend fun googleRevokeAccess() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        val client = GoogleSignIn.getClient(appContext, gso)
        client.revokeAccess().await()
    }

    // Task → suspend helper
    private suspend fun <T> Task<T>.await(): T = suspendCancellableCoroutine { cont ->
        addOnCompleteListener { task ->
            if (task.isSuccessful) cont.resume(task.result)
            else cont.resumeWithException(task.exception ?: RuntimeException("Unknown task error"))
        }
    }


}
