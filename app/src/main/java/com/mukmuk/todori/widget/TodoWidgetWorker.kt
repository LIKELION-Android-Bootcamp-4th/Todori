package com.mukmuk.todori.widget

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.google.firebase.functions.dagger.assisted.Assisted
import com.google.firebase.functions.dagger.assisted.AssistedInject
import com.mukmuk.todori.data.repository.TodoRepository
import com.mukmuk.todori.util.WidgetUtil

@HiltWorker
class TodoWidgetWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repo: TodoRepository
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return try {
            // 투두 불러오기
            val todos = WidgetUtil.loadWidgetTodos(applicationContext)

            // 위젯 새로고침
            TodoWidget.updateAll(applicationContext)

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        fun enqueue(context: Context) {
            val request = OneTimeWorkRequestBuilder<TodoWidgetWorker>().build()
            WorkManager.getInstance(context).enqueue(request)
        }
    }
}