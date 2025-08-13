package com.mukmuk.todori.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.glance.unit.ColorProvider
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.Text
import com.mukmuk.todori.R
import com.mukmuk.todori.util.WidgetUtil


object TodoWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val todos = WidgetUtil.loadWidgetTodos(context)
        provideContent {
            TodoWidgetContent(todos)
        }
    }
}

@Composable
fun TodoWidgetContent(
    todos: List<Pair<String, Boolean>>
) {
    Box(
        modifier = GlanceModifier
            .size(120.dp)
            .padding(16.dp)
            .background(R.color.widgetBgColor),
    ) {
        Column {
            Row {
                Text("TODO")
                Spacer(GlanceModifier.width(10.dp))
                Text("${todos.count { it.second }} / ${todos.size}")
            }
            todos.take(3).forEach { (task, isDone) ->
                Text(task)
            }
        }
    }
}