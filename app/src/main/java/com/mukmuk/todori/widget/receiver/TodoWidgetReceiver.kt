package com.mukmuk.todori.widget.receiver

import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.mukmuk.todori.widget.TodoWidget

class TodoWidgetReceiver: GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = TodoWidget
    //업데이트 지시
    override fun onEnabled(context: Context?) {}
    override fun onReceive(context: Context, intent: Intent) {}
}