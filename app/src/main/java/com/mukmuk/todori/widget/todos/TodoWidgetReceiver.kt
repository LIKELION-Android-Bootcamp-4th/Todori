package com.mukmuk.todori.widget.todos

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.mukmuk.todori.widget.WidgetUpdateDispatcher

class TodoWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TodoWidget()

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
        context?.let {
            WidgetUpdateDispatcher.getDispatcher(it).updateTodos()
        }
    }

    // 이벤트 발생 시 갱신
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when(intent.action) {
            AppWidgetManager.ACTION_APPWIDGET_UPDATE,
            AppWidgetManager.ACTION_APPWIDGET_ENABLED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_USER_UNLOCKED -> {
                WidgetUpdateDispatcher.getDispatcher(context).updateTodos()
            }
        }
    }
}