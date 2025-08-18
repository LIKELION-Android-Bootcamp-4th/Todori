package com.mukmuk.todori.widget.receiver

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.mukmuk.todori.widget.TodoWidget
import com.mukmuk.todori.widget.TodoWidgetWorker

class TodoWidgetReceiver: GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = TodoWidget
    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
        context?.let {
            Log.d("widgetReceiver", "onEnabled실행!")
            TodoWidgetWorker.enqueue(it)
        }
    }
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        when (intent.action) {
            // 갱신
            AppWidgetManager.ACTION_APPWIDGET_UPDATE -> {
                Log.d("widgetReceiver", "update실행!")
                TodoWidgetWorker.enqueue(context)
            }
            // 날짜 바뀌면 갱신
            Intent.ACTION_DATE_CHANGED-> {
                TodoWidgetWorker.enqueue(context)
            }
        }
    }
}