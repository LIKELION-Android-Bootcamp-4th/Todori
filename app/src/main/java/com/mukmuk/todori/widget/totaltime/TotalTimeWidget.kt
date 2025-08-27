package com.mukmuk.todori.widget.totaltime

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.background
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

    companion object {
        val TOTAL_TIME_PREF_KEY = longPreferencesKey("total_record_time_mills")
        const val ACTION_UPDATE_TOTAL_TIME_WIDGET = "com.mukmuk.todori.ACTION_UPDATE_TOTAL_TIME_WIDGET"
        const val EXTRA_TOTAL_TIME_MILLIS = "extra_total_time_millis"
        val TODAY_DATE_PREF_KEY = stringPreferencesKey("today_date_string")
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
        val millis = prefs[TOTAL_TIME_PREF_KEY] ?: 0L
        val today = prefs[TODAY_DATE_PREF_KEY] ?: LocalDate.now().toString()

        val h = (millis / 1000) / 3600
        val m = (millis / 1000 % 3600) / 60
        val s = (millis / 1000) % 60
        val totalTime = String.format("%02d:%02d:%02d", h, m, s)

        Row (
            modifier = GlanceModifier
                .background(Color(0x50FFFFFF))
                .fillMaxSize()
                .clickable(actionStartActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("todori://app.todori.com/home")
                )
            )),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = today, modifier = GlanceModifier.padding(12.dp), style = WidgetTextStyle.TitleSmallLight)
            Text(text = totalTime, modifier = GlanceModifier.padding(12.dp), style = WidgetTextStyle.TitleLarge)
        }
    }
}
