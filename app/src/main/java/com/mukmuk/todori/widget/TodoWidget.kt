package com.mukmuk.todori.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.mukmuk.todori.R
import com.mukmuk.todori.util.WidgetUtil


object TodoWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val todos = WidgetUtil.loadWidgetTodos("testuser")
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
            .fillMaxSize()
            .background(R.color.widgetBgColor),
    ) {
        Column(modifier = GlanceModifier.padding(16.dp)) {
            Row(modifier = GlanceModifier.fillMaxWidth()) {
                Text(
                    "TODO", style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(GlanceModifier.defaultWeight())
                Text(
                    "${todos.count { it.second }} / ${todos.size}",
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
            Spacer(GlanceModifier.height(16.dp))
            if (todos.isEmpty()) {
                Text("TODO가 없습니다.")
            } else {
                todos.take(5).forEach { (task, isDone) ->
                    Text(task)
                }
            }
        }
    }
}