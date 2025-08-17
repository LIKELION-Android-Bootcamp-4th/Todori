package com.mukmuk.todori.widget

import android.content.Context
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
import com.mukmuk.todori.ui.theme.WidgetTextStyle

class TotalTimeWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            TotalTimeWidgetContent()
        }
    }

    @Composable
    private fun TotalTimeWidgetContent() {
        Row (
            modifier = GlanceModifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "0000년 00월 00일", modifier = GlanceModifier.padding(12.dp), style = WidgetTextStyle.TitleSmallLight)
            Text(text = "00:00:00", modifier = GlanceModifier.padding(12.dp), style = WidgetTextStyle.TitleLarge)
        }
    }
}
