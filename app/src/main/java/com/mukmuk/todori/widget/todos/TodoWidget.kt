package com.mukmuk.todori.widget.todos

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mukmuk.todori.MainActivity
import com.mukmuk.todori.R
import com.mukmuk.todori.data.remote.todo.Todo
import java.time.LocalDate


class TodoWidget : GlanceAppWidget() {
    override val stateDefinition = PreferencesGlanceStateDefinition

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        Log.d("todoWidget", "실행! 위젯id:$id")
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
        Log.d("TodoWidget", "읽은 json: $json, todos: $todos")

        val totalTodos = todos.size
        val completedTodos = todos.count { it.completed }

        val today = LocalDate.now()

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
                        "TODO", style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(GlanceModifier.defaultWeight())
                    Text(
                        "$completedTodos / $totalTodos",
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
                    todos.take(3).forEach { todo ->
                        Row {
                            Text("${todo.title}")
                        }
                    }
                }
            }
        }
    }
}