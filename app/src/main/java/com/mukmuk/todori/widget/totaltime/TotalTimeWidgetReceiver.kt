package com.mukmuk.todori.widget.totaltime

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.mukmuk.todori.widget.TotalTimeWidget

class TotalTimeWidgetReceiver: GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TotalTimeWidget()
}

