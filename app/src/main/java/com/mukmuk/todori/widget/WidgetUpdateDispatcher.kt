package com.mukmuk.todori.widget

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.mukmuk.todori.widget.todos.TodoWorker
import com.mukmuk.todori.widget.goaldaycount.DayCountWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
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

    fun updateDayCountWidget() {
        val req = OneTimeWorkRequestBuilder<DayCountWorker>().build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            DayCountWorker.UNIQUE_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            req
        )
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

    // 자정에 업데이트
    @RequiresApi(Build.VERSION_CODES.O)
    fun scheduleDailyUpdate() {
        val now = LocalDateTime.now()
        val nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay()
        val initialDelay = Duration.between(now, nextMidnight)

        // 투두 위젯
//        val request = PeriodicWorkRequestBuilder<TodoWorker>(24, TimeUnit.HOURS)
//            .setInitialDelay(initialDelay.toMillis(), TimeUnit.MILLISECONDS)
//            .build()
//
//        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
//            "TodoWidgetDailyUpdate",
//            ExistingPeriodicWorkPolicy.REPLACE,
//            request
//        )

        // 디데이 위젯
        val dayCountRequest = PeriodicWorkRequestBuilder<DayCountWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(initialDelay.toMillis(), TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "DayCountWidgetDailyUpdate",
            ExistingPeriodicWorkPolicy.REPLACE,
            dayCountRequest
        )
    }

    fun cancelDailyUpdate() {
        WorkManager.getInstance(context).cancelUniqueWork("TodoWidgetDailyUpdate")
    }
}