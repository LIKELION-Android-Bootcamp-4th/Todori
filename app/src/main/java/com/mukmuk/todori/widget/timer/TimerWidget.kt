package com.mukmuk.todori.widget.timer

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.Text
import com.mukmuk.todori.ui.theme.WidgetTextStyle
import com.mukmuk.todori.widget.LaunchActivityCallback

class TimerWidget : GlanceAppWidget() {
    override val stateDefinition = PreferencesGlanceStateDefinition

    companion object {
        val TOTAL_RECORD_MILLS_KEY = longPreferencesKey("total_record_time_mills")
        val RUNNING_STATE_PREF_KEY = booleanPreferencesKey("is_running")
        val TOGGLE_KEY = ActionParameters.Key<Boolean>("toggle_running")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            TotalTimeWidgetContent()
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    private fun TotalTimeWidgetContent() {
        val prefs = currentState<Preferences>()
        val millis = prefs[TOTAL_RECORD_MILLS_KEY] ?: 0L
        val running = prefs[RUNNING_STATE_PREF_KEY] ?: false

        val h = (millis / 1000) / 3600
        val m = (millis / 1000 % 3600) / 60
        val s = (millis / 1000) % 60
        val totalTime = String.format("%02d:%02d:%02d", h, m, s)

        Row(
            modifier = GlanceModifier
                .background(Color(0x50FFFFFF))
                .fillMaxSize()
                .clickable(
                    actionRunCallback<LaunchActivityCallback>()
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = totalTime,
                modifier = GlanceModifier.padding(12.dp),
                style = WidgetTextStyle.TitleLarge
            )
            Box {
                Button(
                    text = if (running) "정지" else "시작",
                    onClick = actionRunCallback<TimerAction>(
                        parameters = actionParametersOf(
                            TOGGLE_KEY to !running
                        )
                    )
                )
            }
        }
    }
}
