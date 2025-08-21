package com.mukmuk.todori.widget.goaldaycount

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class DayCountWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    companion object {
        const val UNIQUE_WORK_NAME = "Day_count_worker"
    }
    override suspend fun doWork(): Result {
        return try {
            val totalTimeWidget = DayCountWidget()
            val manager = GlanceAppWidgetManager(applicationContext)
            val glanceIds = manager.getGlanceIds(totalTimeWidget.javaClass)

            if (glanceIds.isEmpty()) {
                return Result.success()
            }

            Result.success()
        } catch (e : Exception) {
            Result.failure()
        }
    }
}