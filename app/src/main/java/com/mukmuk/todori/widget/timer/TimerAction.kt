package com.mukmuk.todori.widget.timer

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import com.mukmuk.todori.data.local.datastore.RecordSettingRepository
import com.mukmuk.todori.widget.WidgetEntryPoint
import com.mukmuk.todori.widget.timer.TimerWidget.Companion.RUNNING_STATE_PREF_KEY
import com.mukmuk.todori.widget.timer.TimerWidget.Companion.TOGGLE_KEY
import com.mukmuk.todori.widget.totaltime.TotalTimeWidget
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.first

class TimerAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val newRunning = parameters[TOGGLE_KEY] ?: false

        // 위젯의 DataStore 상태를 직접 업데이트
        updateAppWidgetState(context, glanceId) { prefs ->
            prefs[RUNNING_STATE_PREF_KEY] = newRunning
        }

        // 위젯 UI 업데이트를 트리거
        TimerWidget().update(context, glanceId)
    }
}