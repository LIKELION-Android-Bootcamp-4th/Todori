package com.mukmuk.todori.widget.todos

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.mukmuk.todori.widget.WidgetUpdateDispatcher

class TodoWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TodoWidget()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
        context?.let {
            val dispatcher = WidgetUpdateDispatcher.getDispatcher(it)
            dispatcher.updateTodos()
            dispatcher.scheduleDailyUpdate()   // 위젯 설치 → 자정 투두 시작
        }
    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
        context?.let {
            val dispatcher = WidgetUpdateDispatcher.getDispatcher(it)
            dispatcher.cancelDailyUpdate()     // 위젯 모두 제거 → 자정 투두 취소
        }
    }

    // 이벤트 발생 시 갱신
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when(intent.action) {
            AppWidgetManager.ACTION_APPWIDGET_UPDATE,
            AppWidgetManager.ACTION_APPWIDGET_ENABLED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_DATE_CHANGED,
            Intent.ACTION_USER_UNLOCKED -> {
                WidgetUpdateDispatcher.getDispatcher(context).updateTodos()
            }
        }
    }
}