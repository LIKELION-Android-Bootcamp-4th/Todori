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
import androidx.core.app.NotificationCompat
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import com.mukmuk.todori.R
import com.mukmuk.todori.data.local.datastore.RecordSettingRepository
import com.mukmuk.todori.widget.WidgetEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first

class TimerService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private lateinit var repository: RecordSettingRepository
    private lateinit var notificationManager: NotificationManager
    private var timerJob: Job? = null

    companion object {
        const val CHANNEL_ID = "TimerServiceChannel"
        const val NOTIFICATION_ID = 1
        const val ACTION_RESET = "com.mukmuk.todori.widget.timer.RESET"
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

        when (intent?.action) {
            ACTION_RESET -> {
                serviceScope.launch {
                    repository.saveRunningState(false)

                    val glanceIds = GlanceAppWidgetManager(applicationContext)
                        .getGlanceIds(TimerWidget::class.java)

                    glanceIds.forEach { id ->
                        updateAppWidgetState(applicationContext, id) { prefs ->
                            prefs[TimerWidget.TOTAL_RECORD_MILLS_KEY] = 0L
                            prefs[TimerWidget.RUNNING_STATE_PREF_KEY] = false
                        }
                        TimerWidget().update(applicationContext, id)
                    }
                }
                stopSelf()
                return START_NOT_STICKY
            }
        }

        startForeground(NOTIFICATION_ID, createNotification())

        startTimerLoop()

        return START_STICKY
    }

    private fun startTimerLoop() {
        timerJob?.cancel()
        timerJob = serviceScope.launch {
            repository.saveRunningState(true)

            // 초기값 설정
            var totalTime = repository.totalRecordTimeFlow.first()

            repository.totalRecordTimeFlow.collectLatest { latestTime ->
                totalTime = latestTime

                // 위젯이 있으면 업데이트
                val glanceIds = GlanceAppWidgetManager(applicationContext)
                    .getGlanceIds(TimerWidget::class.java)

                if (glanceIds.isNotEmpty()) {
                    glanceIds.forEach { id ->
                        updateAppWidgetState(applicationContext, id) { prefs ->
                            prefs[TimerWidget.TOTAL_RECORD_MILLS_KEY] = totalTime
                            prefs[TimerWidget.RUNNING_STATE_PREF_KEY] = true
                        }
                        TimerWidget().update(applicationContext, id)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timerJob?.cancel()
        serviceScope.cancel()
        notificationManager.cancel(NOTIFICATION_ID)

        serviceScope.launch {
            repository.saveRunningState(false)
            val finalTime = repository.totalRecordTimeFlow.first()
            val glanceIds = GlanceAppWidgetManager(applicationContext)
                .getGlanceIds(TimerWidget::class.java)

            glanceIds.forEach { id ->
                updateAppWidgetState(applicationContext, id) { prefs ->
                    prefs[TimerWidget.TOTAL_RECORD_MILLS_KEY] = finalTime
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
            PendingIntent.getActivity(
                this,
                0,
                launchIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Todori 타이머 실행 중")
            .setContentText("타이머가 백그라운드에서 실행 중입니다.")
            .setSmallIcon(R.drawable.ic_fire1)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }
}
