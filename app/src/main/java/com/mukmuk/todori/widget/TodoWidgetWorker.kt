package com.mukmuk.todori.widget

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.updateAll
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.functions.dagger.assisted.Assisted
import com.google.firebase.functions.dagger.assisted.AssistedInject
import com.mukmuk.todori.util.WidgetUtil
import kotlinx.coroutines.tasks.await

@HiltWorker
class TodoWidgetWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return try {
            // 위젯 새로고침
            Log.d("widgetWorker", "doWork실행!")
            TodoWidget.updateAll(applicationContext)
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    companion object {
        fun enqueue(context: Context) {
            Log.d("widgetWorker", "enqueue실행!")
            val request = OneTimeWorkRequestBuilder<TodoWidgetWorker>().build()
            WorkManager.getInstance(context).enqueue(request)
        }
    }
}