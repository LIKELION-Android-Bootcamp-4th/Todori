package com.mukmuk.todori.widget.todos

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.Text
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mukmuk.todori.MainActivity
import com.mukmuk.todori.R
import com.mukmuk.todori.data.remote.todo.Todo
import com.mukmuk.todori.ui.theme.WidgetTextStyle


class TodoWidget : GlanceAppWidget() {
    override val stateDefinition = PreferencesGlanceStateDefinition

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            TodoWidgetContent()
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun TodoWidgetContent() {
        val PREF_KEY = stringPreferencesKey("today_todos_widget")
        val prefs = currentState<Preferences>()

        val json = prefs[PREF_KEY] ?: "[]"
        val todos: List<Todo> = Gson().fromJson(json, object : TypeToken<List<Todo>>() {}.type)

        val totalTodos = todos.size
        val completedTodos = todos.count { it.completed }

        val pendingTodos = todos.filter { !it.completed }

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(R.color.widgetBgColor)
                .clickable(
                    actionStartActivity<MainActivity>()
                )
        ) {
            Column(modifier = GlanceModifier.padding(16.dp)) {
                Row(modifier = GlanceModifier.fillMaxWidth()) {
                    Text(
                        "TODO", style = WidgetTextStyle.TitleMedium
                    )
                    Spacer(GlanceModifier.defaultWeight())
                    Text(
                        "$completedTodos / $totalTodos",
                        style = WidgetTextStyle.TitleMediumLight
                    )
                }
                Spacer(GlanceModifier.height(16.dp))
                if (todos.isEmpty()) {
                    Text("TODO가 없습니다.", style = WidgetTextStyle.TitleSmallNormal)
                } else {
                    pendingTodos.take(5).forEach { todo ->
                        Row {
                            Text(todo.title, style = WidgetTextStyle.TitleSmallNormal)
                        }
                    }
                }
            }
        }
    }
}