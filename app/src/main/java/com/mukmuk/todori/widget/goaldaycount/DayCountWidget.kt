package com.mukmuk.todori.widget.goaldaycount

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.glance.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.Text
import com.google.gson.Gson
import com.mukmuk.todori.MainActivity
import com.mukmuk.todori.R
import com.mukmuk.todori.data.remote.goal.Goal
import com.mukmuk.todori.ui.theme.WidgetTextStyle
import com.mukmuk.todori.widget.TodoLaunchActivityCallback
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.todayIn
import java.time.temporal.ChronoUnit

class DayCountWidget : GlanceAppWidget() {
    override val stateDefinition = PreferencesGlanceStateDefinition

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        Log.d("D-DayWidget", "provideGlance 호출됨. 위젯 ID: $id")
        provideContent {
            GoalDayCountWidgetContent()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun GoalDayCountWidgetContent() {
        val PREF_KEY = stringPreferencesKey("day_count_widget")
        val prefs = currentState<Preferences>()
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val json = prefs[PREF_KEY] ?: "null"

        val selectedGoal: Goal? = Gson().fromJson(json, Goal::class.java)
        val dDay: Int? = selectedGoal?.let {
            val end = kotlinx.datetime.LocalDate.parse(it.endDate)
            ChronoUnit.DAYS.between(today.toJavaLocalDate(), end.toJavaLocalDate()).toInt()
        }
        val goalTitle = selectedGoal?.title ?: "설정 목표 없음"

        fun ellipsize(text: String, maxLength: Int): String {
            return if (text.length > maxLength) {
                text.take(maxLength) + "…"
            } else text
        }

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(R.color.widgetBgColor)
                .clickable(
                    actionRunCallback<TodoLaunchActivityCallback>()
                ),
            contentAlignment = Alignment.Center
        ) {
            Column() {
                Text(
                    "가장 근접한 목표",
                    style = WidgetTextStyle.TitleTiny,
                    modifier = GlanceModifier.padding(horizontal = 16.dp)
                )

                Row(
                    modifier = GlanceModifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        ellipsize(goalTitle, 12),
                        style = WidgetTextStyle.TitleMediumLight
                    )
                    Spacer(GlanceModifier.defaultWeight())
                    if (dDay != null) {
                        Text(
                            text = if (dDay > 0) "D-$dDay" else if (dDay == 0) "D-DAY" else "",
                            style = WidgetTextStyle.TitleMedium
                        )
                    } else {
                        Text("")
                    }
                }
            }
        }
    }
}
