package com.mukmuk.todori.widget.receiver

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
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
            // 주기적으로 업데이트
            TodoWidgetWorker.enqueue(it)
        }
    }
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        when (intent.action) {
            AppWidgetManager.ACTION_APPWIDGET_UPDATE -> {
                TodoWidgetWorker.enqueue(context)
            }
            Intent.ACTION_DATE_CHANGED-> {
                TodoWidgetWorker.enqueue(context)
            }
        }
    }
}