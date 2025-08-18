package com.mukmuk.todori.widget.totaltime

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.Text
import com.mukmuk.todori.MainActivity
import com.mukmuk.todori.data.local.datastore.RecordSettingRepository
import com.mukmuk.todori.ui.theme.WidgetTextStyle
import com.mukmuk.todori.widget.WidgetEntryPoint
import dagger.hilt.android.EntryPointAccessors
import java.time.LocalDate
import javax.inject.Inject

class TotalTimeWidget : GlanceAppWidget() {
    override val stateDefinition = PreferencesGlanceStateDefinition

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        Log.d("TotalTimeWidget", "provideGlance 호출됨. 위젯 ID: $id")
        provideContent {
            TotalTimeWidgetContent()
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    private fun TotalTimeWidgetContent() {
        val PREF_KEY = longPreferencesKey("total_record_time_mills")

        val prefs = currentState<Preferences>()
        val millis = prefs[PREF_KEY] ?: 0L

        val h = (millis / 1000) / 3600
        val m = (millis / 1000 % 3600) / 60
        val s = (millis / 1000) % 60
        val totalTime = String.format("%02d:%02d:%02d", h, m, s)
        val today = LocalDate.now().toString()

        Row (
            modifier = GlanceModifier.fillMaxSize().clickable(actionStartActivity<MainActivity>()),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = today, modifier = GlanceModifier.padding(12.dp), style = WidgetTextStyle.TitleSmallLight)
            Text(text = totalTime, modifier = GlanceModifier.padding(12.dp), style = WidgetTextStyle.TitleLarge)
        }
    }
}
