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
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.gson.Gson
import com.mukmuk.todori.data.repository.TodoRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.EntryPointAccessors
import java.time.LocalDate

@HiltWorker
class TodoWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    companion object {
        const val UNIQUE_WORK_NAME = "Todo_worker"
    }

    private val todoRepository: TodoRepository by lazy {
        val entryPoint = EntryPointAccessors.fromApplication(
            appContext,
            TodoWorkerEntryPoint::class.java
        )
        entryPoint.todoRepository()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        return try {
            val todoWidget = TodoWidget()
            val manager = GlanceAppWidgetManager(applicationContext)
            val glanceIds = manager.getGlanceIds(todoWidget.javaClass)

            val uid = Firebase.auth.currentUser?.uid.toString()

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