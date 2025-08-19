package com.mukmuk.todori.widget

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mukmuk.todori.widget.todos.TodoWidget
import com.mukmuk.todori.widget.totaltime.TotalTimeWidget
import dagger.hilt.android.EntryPointAccessors

class UpdateWidgetWorker (
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        Log.d("worker", "doWork 실행!")
        return try {
            val recordSettingRepository = EntryPointAccessors.fromApplication(
                applicationContext,
                WidgetEntryPoint::class.java
            ).recordSettingRepository()

            recordSettingRepository.saveTotalRecordTime(0L)

            val totalTimeWidget = TotalTimeWidget()
            val manager = GlanceAppWidgetManager(applicationContext)
            val glanceIds = manager.getGlanceIds(totalTimeWidget.javaClass)

            if (glanceIds.isEmpty()) {
                return Result.success()
            }

            val PREF_KEY = longPreferencesKey("total_record_time_mills")

            glanceIds.forEach { glanceId ->
                updateAppWidgetState(
                    context = applicationContext,
                    glanceId = glanceId
                ) { prefs ->
                    prefs[PREF_KEY] = 0L
                }

                totalTimeWidget.update(applicationContext, glanceId)
            }


            val todoWidget = TodoWidget()
            val todoManager = GlanceAppWidgetManager(applicationContext)
            val todoGlanceIds = todoManager.getGlanceIds(todoWidget.javaClass)

            todoGlanceIds.forEach { glanceId ->
                updateAppWidgetState(
                    context = applicationContext,
                    glanceId = glanceId
                ) { prefs ->
                    prefs[stringPreferencesKey("today_todo_widget")]
                }

                todoWidget.update(applicationContext, glanceId)
            }

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        const val WORK_TAG = "MidnightResetWorker"
        const val WORK_NAME = "MidnightResetWork"
    }
}