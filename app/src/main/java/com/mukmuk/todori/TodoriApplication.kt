package com.mukmuk.todori

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.kakao.sdk.common.KakaoSdk
import com.navercorp.nid.NaverIdLoginSDK
import dagger.hilt.android.HiltAndroidApp

object PushChannels { const val DEFAULT = "todori_default" }

@HiltAndroidApp
class TodoriApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, getString(R.string.kakao_app_key))

        NaverIdLoginSDK.initialize(
            this,
            getString(R.string.naver_client_id),
            getString(R.string.naver_client_secret),
            getString(R.string.app_name)
        )

        val channel = NotificationChannel(
            PushChannels.DEFAULT,
            "기본 알림",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }
}
