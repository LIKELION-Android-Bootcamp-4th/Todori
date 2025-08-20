package com.mukmuk.todori.widget.timer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import com.mukmuk.todori.R
import com.mukmuk.todori.data.local.datastore.RecordSettingRepository
import com.mukmuk.todori.widget.WidgetEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlin.time.Duration.Companion.seconds

class TimerService : Service() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var timerJob: Job? = null
    private lateinit var repository: RecordSettingRepository
    private lateinit var notificationManager: NotificationManager

    companion object {
        const val CHANNEL_ID = "TimerServiceChannel"
        const val NOTIFICATION_ID = 1
        const val EXTRA_GLANCE_ID_STRING = "extra_glance_id_string"
    }

    override fun onCreate() {
        super.onCreate()
        repository = EntryPointAccessors.fromApplication(
            applicationContext,
            WidgetEntryPoint::class.java
        ).recordSettingRepository()

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification())

        serviceScope.launch {
            repository.saveRunningState(true)

            val glanceIds = GlanceAppWidgetManager(applicationContext)
                .getGlanceIds(TimerWidget::class.java)

            if (glanceIds.isEmpty()) {
                stopSelf()
                return@launch
            }

            val initialMillis = repository.totalRecordTimeFlow.first()
            glanceIds.forEach { id ->
                updateAppWidgetState(applicationContext, id) { prefs ->
                    prefs[TimerWidget.TOTAL_RECORD_MILLS_KEY] = initialMillis
                    prefs[TimerWidget.RUNNING_STATE_PREF_KEY] = true
                }
                TimerWidget().update(applicationContext, id)
            }

            startTimer(glanceIds)
        }

        return START_STICKY
    }

    private fun startTimer(glanceIds: List<GlanceId>) {
        timerJob?.cancel()
        timerJob = serviceScope.launch(Dispatchers.IO) {
            var currentTotalMillis = repository.totalRecordTimeFlow.first()

            while (isActive) {
                delay(1.seconds)

                currentTotalMillis += 1000L
                repository.saveTotalRecordTime(currentTotalMillis)

                glanceIds.forEach { id ->
                    updateAppWidgetState(applicationContext, id) { prefs ->
                        prefs[TimerWidget.TOTAL_RECORD_MILLS_KEY] = currentTotalMillis
                    }
                    TimerWidget().update(applicationContext, id)
                }

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timerJob?.cancel()
        serviceScope.cancel()
        notificationManager.cancel(NOTIFICATION_ID)
        CoroutineScope(Dispatchers.IO).launch {
            repository.saveRunningState(false)

            val glanceIds = GlanceAppWidgetManager(applicationContext)
                .getGlanceIds(TimerWidget::class.java)

            val finalMillis = repository.totalRecordTimeFlow.first()

            glanceIds.forEach { id ->
                updateAppWidgetState(applicationContext, id) { prefs ->
                    prefs[TimerWidget.TOTAL_RECORD_MILLS_KEY] = finalMillis + 1000L
                    prefs[TimerWidget.RUNNING_STATE_PREF_KEY] = false
                }
                TimerWidget().update(applicationContext, id)
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Timer Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val pendingIntent = packageManager.getLaunchIntentForPackage(packageName)?.let { launchIntent ->
            PendingIntent.getActivity(this, 0, launchIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Todori 타이머 실행 중")
            .setContentText("타이머 위젯이 매 초 업데이트됩니다. \n배터리 사용량이 늘어날 수 있어요.")
            .setSmallIcon(R.drawable.ic_fire1)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }
}
