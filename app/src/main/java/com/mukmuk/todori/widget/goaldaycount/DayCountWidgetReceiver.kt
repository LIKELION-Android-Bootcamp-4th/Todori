package com.mukmuk.todori.widget.goaldaycount

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.mukmuk.todori.widget.WidgetUpdateDispatcher

class DayCountWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = DayCountWidget()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
        context?.let {
            val dispatcher = WidgetUpdateDispatcher.getDispatcher(it)
            dispatcher.updateDayCountWidget()
            dispatcher.scheduleDailyUpdate()
        }
    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
        context?.let {
            val dispatcher = WidgetUpdateDispatcher.getDispatcher(it)
            dispatcher.cancelDailyUpdate()
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when(intent.action) {
            AppWidgetManager.ACTION_APPWIDGET_UPDATE,
            AppWidgetManager.ACTION_APPWIDGET_ENABLED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_DATE_CHANGED,
            Intent.ACTION_USER_UNLOCKED -> {
                WidgetUpdateDispatcher.getDispatcher(context).updateDayCountWidget()
            }
        }
    }
}