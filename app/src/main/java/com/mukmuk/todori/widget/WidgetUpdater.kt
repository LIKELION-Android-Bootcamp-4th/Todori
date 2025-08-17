package com.mukmuk.todori.widget

import android.content.Context
import androidx.glance.appwidget.updateAll
import com.mukmuk.todori.widget.totaltime.TotalTimeWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object WidgetUpdater {
    fun update(context: Context) {
        CoroutineScope(Dispatchers.Default).launch {
            TotalTimeWidget().updateAll(context)
        }
    }
}