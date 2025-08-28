package com.mukmuk.todori.data.service

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mukmuk.todori.util.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val ioScope = CoroutineScope(Dispatchers.IO)

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.data["title"] ?: remoteMessage.notification?.title ?: ""
        val body  = remoteMessage.data["body"]  ?: remoteMessage.notification?.body  ?: ""
        val deeplink = remoteMessage.data["deeplink"]

        NotificationHelper.show(
            context = applicationContext,
            title = title,
            body = body,
            deeplink = deeplink
        )
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "onNewToken: $token")
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        ioScope.launch { runCatching { saveTokenToFirestore(uid, token) } }
    }

    companion object {
        fun ensureTokenSync() {
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
            FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                CoroutineScope(Dispatchers.IO).launch {
                    runCatching { saveTokenStatic(uid, token) }
                }
            }
        }

        suspend fun cleanupOnLogout() {
            val auth = FirebaseAuth.getInstance()
            val uid = auth.currentUser?.uid ?: return

            val fm = FirebaseMessaging.getInstance()
            val token = runCatching { fm.token.await() }.getOrNull()

            if (!token.isNullOrBlank()) {
                runCatching { deleteTokenFromFirestore(uid, token) }
            }

            // 기기 로컬 토큰 삭제 → 다음 실행 시 새 토큰 발급
            runCatching { fm.deleteToken().await() }
        }


        private suspend fun saveTokenStatic(uid: String, token: String) {
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
            val db = FirebaseFirestore.getInstance()
            val doc = mapOf(
                "token" to token,
                "platform" to "android",
                "updatedAt" to FieldValue.serverTimestamp()
            )
            db.collection("users").document(uid)
                .collection("fcmTokens").document(token)
                .set(doc, SetOptions.merge()).await()
        }

        private suspend fun deleteTokenFromFirestore(uid: String, token: String) {
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(uid)
                .collection("fcmTokens").document(token)
                .delete()
                .await()
        }
    }

    private suspend fun saveTokenToFirestore(uid: String, token: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()
        val doc = mapOf(
            "token" to token,
            "platform" to "android",
            "updatedAt" to FieldValue.serverTimestamp()
        )
        db.collection("users").document(uid)
            .collection("fcmTokens").document(token)
            .set(doc, SetOptions.merge()).await()
    }
}
