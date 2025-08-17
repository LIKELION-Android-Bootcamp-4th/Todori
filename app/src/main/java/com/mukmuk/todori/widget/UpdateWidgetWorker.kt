package com.mukmuk.todori.widget

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mukmuk.todori.widget.totaltime.TotalTimeWidget

class UpdateWidgetWorker (
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        return try {
            TotalTimeWidget().updateAll(this@UpdateWidgetWorker.applicationContext)
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        const val WORK_TAG = "TotalTimeWidgetUpdate"
    }
}