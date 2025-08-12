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
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.Text
import com.mukmuk.todori.R


object TodoWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            TodoWidgetContent(
                todos = listOf(
                    "숨 쉬기" to false,
                    "오류 잡기" to false,
                    "팀원들 응원하기" to true
                )
            )
        }
    }
}

@Composable
fun TodoWidgetContent(
    todos: List<Pair<String, Boolean>>
) {

    Column(modifier = GlanceModifier.size(110.dp)) {
        Box (
            modifier = GlanceModifier
                .size(110.dp)
                .background(R.color.widgetBgColor)
        ) {
            Column {
                Row {
                    Text("TODO")
                    Spacer(GlanceModifier.width(8.dp))
                    Text("${todos.count { it.second }} / ${todos.size}")
                }
                todos.take(3).forEach { (task, isDone) ->
                    Text("$task")
                }
            }
        }

//        todos.take(3).forEach { (task, isDone) ->
//            TodoItemRow(title = task, isDone = isDone)
//        }
    }

}