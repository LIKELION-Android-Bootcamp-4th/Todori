package com.mukmuk.todori.widget.todos

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.mukmuk.todori.data.repository.TodoRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDate

@HiltWorker
class TodoWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val todoRepository: TodoRepository
) : CoroutineWorker(appContext, params) {
    companion object {
        const val UNIQUE_WORK_NAME = "Todo_worker"
    }

    // 투두 목록을 가져오고 위젯 갱신
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        return try {
            val todoWidget = TodoWidget()
            val manager = GlanceAppWidgetManager(applicationContext)
            val glanceIds = manager.getGlanceIds(todoWidget.javaClass)

            // 오늘 날짜 투두 가져오기
            val uid = inputData.getString("uid") ?: return Result.failure()
            val todayTodos = todoRepository.getTodosByDate(uid, LocalDate.now())

            val json = Gson().toJson(todayTodos)
            val PREF_KEY = stringPreferencesKey("today_todos_widget")

            glanceIds.forEach { glanceId ->
                updateAppWidgetState(context = applicationContext, glanceId = glanceId) { prefs ->
                    prefs[PREF_KEY] = json
                }
                todoWidget.update(applicationContext, glanceId)
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}