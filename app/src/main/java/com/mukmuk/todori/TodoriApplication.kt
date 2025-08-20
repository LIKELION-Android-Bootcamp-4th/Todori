package com.mukmuk.todori

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.firebase.auth.FirebaseAuth
import com.kakao.sdk.common.KakaoSdk
import com.mukmuk.todori.widget.UpdateWidgetWorker
import com.mukmuk.todori.widget.todos.TodoWorker
import com.navercorp.nid.NaverIdLoginSDK
import dagger.hilt.android.HiltAndroidApp
import java.util.Calendar
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class TodoriApplication : Application(), Configuration.Provider {
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, getString(R.string.kakao_app_key))

        NaverIdLoginSDK.initialize(
            this,
            getString(R.string.naver_client_id),
            getString(R.string.naver_client_secret),
            getString(R.string.app_name)
        )

        scheduleResetWorker()
    }

    override val workManagerConfiguration: Configuration = Configuration.Builder()
        .setMinimumLoggingLevel(Log.DEBUG)
        .build()

    private fun scheduleResetWorker() {
        val currentDate = Calendar.getInstance()
        val dueDate = Calendar.getInstance()

        dueDate.set(Calendar.HOUR_OF_DAY, 0)
        dueDate.set(Calendar.MINUTE, 0)
        dueDate.set(Calendar.SECOND, 0)

        if (dueDate.before(currentDate)) {
            dueDate.add(Calendar.HOUR_OF_DAY, 24)
        }

        val timeDiff = dueDate.timeInMillis - currentDate.timeInMillis

        val midnightResetRequest =
            PeriodicWorkRequestBuilder<UpdateWidgetWorker>(
                1, TimeUnit.DAYS
            )
                .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                .addTag(UpdateWidgetWorker.WORK_TAG)
                .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            UpdateWidgetWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            midnightResetRequest
        )

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            val data = workDataOf("uid" to uid)
            val midnightTodoResetRequest =
                PeriodicWorkRequestBuilder<TodoWorker>(
                    1, TimeUnit.DAYS
                )
                    .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                    .addTag(UpdateWidgetWorker.WORK_TAG)
                    .build()

            WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                TodoWorker.UNIQUE_WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                midnightTodoResetRequest
            )
        }
    }
}
