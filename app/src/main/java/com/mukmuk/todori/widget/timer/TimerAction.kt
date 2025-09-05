package com.mukmuk.todori.widget.timer

import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresApi
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import com.mukmuk.todori.widget.WidgetEntryPoint
import dagger.hilt.android.EntryPointAccessors

class TimerAction : ActionCallback {
    @RequiresApi(26)
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val repository = EntryPointAccessors.fromApplication(
            context.applicationContext,
            WidgetEntryPoint::class.java
        ).recordSettingRepository()

        val newRunning = parameters[TimerWidget.TOGGLE_KEY] ?: false

        val serviceIntent = Intent(context, TimerService::class.java).apply {
            putExtra(TimerService.EXTRA_GLANCE_ID_STRING, glanceId.toString())
        }

        if (newRunning) {
            context.startForegroundService(serviceIntent)
        } else {
            context.stopService(serviceIntent)
        }

        repository.saveRunningState(newRunning)

        updateAppWidgetState(context, glanceId) { prefs ->
            prefs[TimerWidget.RUNNING_STATE_PREF_KEY] = newRunning
        }

        TimerWidget().update(context, glanceId)
    }
}
