package com.mukmuk.todori.widget

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.mukmuk.todori.widget.todos.TodoWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WidgetUpdateDispatcher @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun updateTodos() {
        val req = OneTimeWorkRequestBuilder<TodoWorker>().build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            TodoWorker.UNIQUE_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            req
        )
    }

    fun updateCurrentReadingBook() {
        updateTodos()
    }

    companion object {
        @Volatile
        private var instance: WidgetUpdateDispatcher? = null

        fun getDispatcher(context: Context): WidgetUpdateDispatcher {
            return instance ?: synchronized(this) {
                instance ?: WidgetUpdateDispatcher(context.applicationContext).also { instance = it }
            }
        }
    }
}