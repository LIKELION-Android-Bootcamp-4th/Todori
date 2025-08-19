package com.mukmuk.todori.widget.timer

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
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
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.Text
import com.mukmuk.todori.ui.theme.WidgetTextStyle
import com.mukmuk.todori.widget.WidgetEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last

class TimerWidget : GlanceAppWidget() {
    override val stateDefinition = PreferencesGlanceStateDefinition

    companion object {
        val TOTAL_RECORD_MILLS_KEY = longPreferencesKey("total_record_time_mills")
        val RUNNING_STATE_PREF_KEY = booleanPreferencesKey("is_running")
        val TOGGLE_KEY = ActionParameters.Key<Boolean>("toggle_running")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = EntryPointAccessors.fromApplication(
            context,
            WidgetEntryPoint::class.java
        ).recordSettingRepository()

        val initialMillis = repository.totalRecordTimeFlow.first()
        val initialRunningState = repository.runningStateFlow.first()

        updateAppWidgetState(context, id) { prefs ->
            prefs[TOTAL_RECORD_MILLS_KEY] = initialMillis
            prefs[RUNNING_STATE_PREF_KEY] = initialRunningState
        }
        provideContent {
            TotalTimeWidgetContent(initialMillis = initialMillis, running = initialRunningState)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    private fun TotalTimeWidgetContent(initialMillis: Long, running: Boolean) {
        val h = (initialMillis / 1000) / 3600
        val m = (initialMillis / 1000 % 3600) / 60
        val s = (initialMillis / 1000) % 60
        val totalTime = String.format("%02d:%02d:%02d", h, m, s)

        Row(
            modifier = GlanceModifier.fillMaxSize().clickable(
                actionStartActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("todori://app.todori.com/home")
                    )
                )
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
