package com.mukmuk.todori

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.firebase.auth.FirebaseAuth
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.annotation.RequiresApi
import com.kakao.sdk.common.KakaoSdk
import com.mukmuk.todori.widget.UpdateWidgetWorker
import com.mukmuk.todori.widget.todos.TodoWorker
import com.navercorp.nid.NaverIdLoginSDK
import dagger.hilt.android.HiltAndroidApp
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

object PushChannels { const val DEFAULT = "todori_default" }

@HiltAndroidApp
class TodoriApplication : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        WorkManager.initialize(this, workManagerConfiguration)
        KakaoSdk.init(this, getString(R.string.kakao_app_key))

        NaverIdLoginSDK.initialize(
            this,
            getString(R.string.naver_client_id),
            getString(R.string.naver_client_secret),
            getString(R.string.app_name)
        )

        scheduleResetWorker()
    }

    override val workManagerConfiguration: Configuration
        get() {
            return Configuration.Builder()
                .setWorkerFactory(workerFactory)
                .setMinimumLoggingLevel(android.util.Log.VERBOSE)
                .build()
        }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun scheduleResetWorker() {
        val now = Calendar.getInstance()
        val midnight = Calendar.getInstance().apply {
            if (now.get(Calendar.HOUR_OF_DAY) >= 0 && now.get(Calendar.HOUR_OF_DAY) < 1) {
                add(Calendar.DAY_OF_YEAR, 1)
            } else {
                add(Calendar.DAY_OF_YEAR, 1)
            }
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        var initialDelay = midnight.timeInMillis - now.timeInMillis
        if (initialDelay < 0) {
            initialDelay += TimeUnit.DAYS.toMillis(1)
        }

        Log.d("WorkScheduler", "Current time: ${now.time}")
        Log.d("WorkScheduler", "Next midnight target: ${midnight.time}")
        Log.d("WorkScheduler", "Calculated initial delay for worker: $initialDelay ms")

        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()
        val timeDiff = midnight.timeInMillis - now.timeInMillis

        val midnightResetRequest =
            PeriodicWorkRequestBuilder<UpdateWidgetWorker>(
                1, TimeUnit.DAYS,
                15, TimeUnit.MINUTES
            )
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .setConstraints(constraints)
                .addTag(UpdateWidgetWorker.WORK_TAG)
                .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            UpdateWidgetWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            midnightResetRequest
        )
        Log.d("WorkScheduler", "Midnight reset worker scheduled with constraints and specific flexPeriod.")

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
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
        val channel = NotificationChannel(
            PushChannels.DEFAULT,
            "기본 알림",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }
}
