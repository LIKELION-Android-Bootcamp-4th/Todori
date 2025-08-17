package com.mukmuk.todori.widget.totaltime

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.Text
import com.mukmuk.todori.data.local.datastore.RecordSettingRepository
import com.mukmuk.todori.ui.theme.WidgetTextStyle
import com.mukmuk.todori.widget.WidgetEntryPoint
import dagger.hilt.android.EntryPointAccessors
import java.time.LocalDate
import javax.inject.Inject

class TotalTimeWidget : GlanceAppWidget() {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val hiltEntryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            WidgetEntryPoint::class.java
        )
        val repository = hiltEntryPoint.recordSettingRepository()

        val today = LocalDate.now().toString()
        val totalTime = repository.getTotalTime()

        provideContent {
            TotalTimeWidgetContent(today, totalTime)
        }
    }

    @Composable
    private fun TotalTimeWidgetContent(today: String, totalTime: String) {
        Row (
            modifier = GlanceModifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = today, modifier = GlanceModifier.padding(12.dp), style = WidgetTextStyle.TitleSmallLight)
            Text(text = totalTime, modifier = GlanceModifier.padding(12.dp), style = WidgetTextStyle.TitleLarge)
        }
    }
}
