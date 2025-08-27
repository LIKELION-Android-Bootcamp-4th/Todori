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
        ioScope.launch { runCatching { saveTokenToFirestore(token) } }
    }

    companion object {
        fun ensureTokenSync() {
            FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                CoroutineScope(Dispatchers.IO).launch {
                    runCatching { saveTokenStatic(token) }
                }
            }
        }

        private suspend fun saveTokenStatic(token: String) {
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

    private suspend fun saveTokenToFirestore(token: String) {
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
