package com.mukmuk.todori.widget.goaldaycount

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.gson.Gson
import com.mukmuk.todori.data.repository.GoalRepository
import dagger.hilt.android.EntryPointAccessors
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.todayIn
import java.time.temporal.ChronoUnit

class DayCountWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    companion object {
        const val UNIQUE_WORK_NAME = "Day_count_worker"
    }

    private val dayCountRepository: GoalRepository by lazy {
        val entryPoint = EntryPointAccessors.fromApplication(
            appContext,
            DayCountWorkerEntryPoint::class.java
        )
        entryPoint.goalRepository()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        return try {
            val dayCountWidget = DayCountWidget()
            val manager = GlanceAppWidgetManager(applicationContext)
            val glanceIds = manager.getGlanceIds(dayCountWidget.javaClass)

            val uid = Firebase.auth.currentUser?.uid.toString()

            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            val selectedGoal = dayCountRepository.getGoals(uid)
                .minByOrNull { goal ->
                    val end = kotlinx.datetime.LocalDate.parse(goal.endDate)
                    ChronoUnit.DAYS.between(today.toJavaLocalDate(), end.toJavaLocalDate())
                }

            val json = Gson().toJson(selectedGoal)
            val PREF_KEY = stringPreferencesKey("day_count_widget")

            glanceIds.forEach { glanceId ->
                updateAppWidgetState(applicationContext, glanceId) { prefs ->
                    prefs[PREF_KEY] = json
                }
                dayCountWidget.update(applicationContext, glanceId)
            }

            Result.success()
        } catch (e : Exception) {
            Result.failure()
        }
    }
}