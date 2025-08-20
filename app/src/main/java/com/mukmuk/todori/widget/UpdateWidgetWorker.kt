package com.mukmuk.todori.widget

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mukmuk.todori.widget.timer.TimerWidget
import com.mukmuk.todori.widget.timer.TimerWidget.Companion.RUNNING_STATE_PREF_KEY
import com.mukmuk.todori.widget.timer.TimerWidget.Companion.TOTAL_RECORD_MILLS_KEY
import com.mukmuk.todori.widget.totaltime.TotalTimeWidget
import com.mukmuk.todori.widget.totaltime.TotalTimeWidget.Companion.TODAY_DATE_PREF_KEY
import com.mukmuk.todori.widget.totaltime.TotalTimeWidget.Companion.TOTAL_TIME_PREF_KEY
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate

class UpdateWidgetWorker (
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        return try {
            val recordSettingRepository = EntryPointAccessors.fromApplication(
                applicationContext,
                WidgetEntryPoint::class.java
            ).recordSettingRepository()

            recordSettingRepository.saveTotalRecordTime(0L)
            recordSettingRepository.saveRunningState(false)
            Log.d("UpdateWidgetWorker", "DataStore total record time reset to 0.")

            val manager = GlanceAppWidgetManager(applicationContext)
            val totalTimeWidgetGlanceIds = manager.getGlanceIds(TotalTimeWidget::class.java)
            val timerWidgetGlanceIds = manager.getGlanceIds(TimerWidget::class.java)

            if (totalTimeWidgetGlanceIds.isEmpty() && timerWidgetGlanceIds.isEmpty()) {
                return Result.success()
            }

            val nextDayDateString = LocalDate.now().toString()

            totalTimeWidgetGlanceIds.forEach { glanceId ->
                updateAppWidgetState(
                    context = applicationContext,
                    glanceId = glanceId
                ) { prefs ->
                    prefs[TOTAL_TIME_PREF_KEY] = 0L
                    prefs[TODAY_DATE_PREF_KEY] = nextDayDateString
                }
                withContext(Dispatchers.Main) {
                    TotalTimeWidget().update(applicationContext, glanceId)
                }
                Log.d("UpdateWidgetWorker", "TotalTimeWidget ${glanceId.toString()} UI updated to 0 and date: $nextDayDateString.")
            }

            timerWidgetGlanceIds.forEach { glanceId ->
                updateAppWidgetState(
                    context = applicationContext,
                    glanceId = glanceId
                ) { prefs ->
                    prefs[TOTAL_RECORD_MILLS_KEY] = 0L
                    prefs[RUNNING_STATE_PREF_KEY] = false
                }
                withContext(Dispatchers.Main) {
                    TimerWidget().update(applicationContext, glanceId)
                }
                Log.d("UpdateWidgetWorker", "TimerWidget ${glanceId.toString()} UI updated to 0 and running state false.")
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("UpdateWidgetWorker", "Error resetting widgets: ${e.message}", e)

            Result.failure()
        }
    }

    companion object {
        const val WORK_TAG = "MidnightResetWorker"
        const val WORK_NAME = "MidnightResetWork"
    }
}