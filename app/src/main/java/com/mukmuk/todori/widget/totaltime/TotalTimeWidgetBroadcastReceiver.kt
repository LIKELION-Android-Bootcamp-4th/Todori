package com.mukmuk.todori.widget.totaltime

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import com.mukmuk.todori.widget.totaltime.TotalTimeWidget.Companion.ACTION_UPDATE_TOTAL_TIME_WIDGET
import com.mukmuk.todori.widget.totaltime.TotalTimeWidget.Companion.EXTRA_TOTAL_TIME_MILLIS
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TotalTimeWidgetBroadcastReceiver : BroadcastReceiver() {
    @OptIn(DelicateCoroutinesApi::class)
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_UPDATE_TOTAL_TIME_WIDGET) {
            val totalMillis = intent.getLongExtra(EXTRA_TOTAL_TIME_MILLIS, 0L)
            val pendingResult = goAsync()

            GlobalScope.launch(Dispatchers.Default + SupervisorJob()) {
                try {
                    val manager = GlanceAppWidgetManager(context)
                    val glanceIds = manager.getGlanceIds(TotalTimeWidget::class.java)

                    glanceIds.forEach { glanceId ->
                        updateAppWidgetState(context, glanceId) { prefs ->
                            prefs[TotalTimeWidget.TOTAL_TIME_PREF_KEY] = totalMillis
                        }
                        withContext(Dispatchers.Main) {
                            TotalTimeWidget().update(context, glanceId)
                        }
                    }
                } catch (e: Exception) {
                } finally {
                    pendingResult.finish()
                }
            }
        }

    }
}
