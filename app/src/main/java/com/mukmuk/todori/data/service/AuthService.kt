package com.mukmuk.todori.data.service

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AuthService @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
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
        suspendCancellableCoroutine<Unit> { cont ->
            client.signOut().addOnCompleteListener {
                cont.resume(Unit)
            }
        }
    }
}
