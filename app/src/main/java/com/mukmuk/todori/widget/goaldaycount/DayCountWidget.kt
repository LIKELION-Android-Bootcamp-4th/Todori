package com.mukmuk.todori.widget.goaldaycount

import android.content.Context
import android.os.Build
import android.util.Log
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
import com.google.gson.reflect.TypeToken
import com.mukmuk.todori.MainActivity
import com.mukmuk.todori.R
import com.mukmuk.todori.data.remote.goal.Goal
import com.mukmuk.todori.ui.theme.WidgetTextStyle
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
        val PREF_KEY = stringPreferencesKey("today_todos_widget")
        val prefs = currentState<Preferences>()

        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val dDay: Int?
        val goalTitle: String

        val mockGoals = listOf(
            Goal(title = "공부하기", endDate = "2025-08-25"), // 가까운 날짜
            Goal(title = "운동하기", endDate = "2025-09-10")
        )

        val json = prefs[PREF_KEY] ?: "[]"
        val goals: List<Goal> = if (json == "[]") mockGoals else Gson().fromJson(json, object : TypeToken<List<Goal>>() {}.type)
        val selectedGoal = goals
            .filter { it.endDate.isNotBlank() }
            .minByOrNull { goal ->
                val end = kotlinx.datetime.LocalDate.parse(goal.endDate)
                ChronoUnit.DAYS.between(today.toJavaLocalDate(), end.toJavaLocalDate())
            }

        if (selectedGoal != null && selectedGoal.endDate.isNotBlank()) {
            val end = kotlinx.datetime.LocalDate.parse(selectedGoal.endDate)
            dDay = ChronoUnit.DAYS.between(today.toJavaLocalDate(), end.toJavaLocalDate()).toInt()
            goalTitle = selectedGoal.title
        } else {
            dDay = null
            goalTitle = "설정 목표 없음"
        }

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(R.color.widgetBgColor)
                .clickable(
                    actionStartActivity<MainActivity>()
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = GlanceModifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    goalTitle, style = WidgetTextStyle.TitleMedium
                )
                Spacer(GlanceModifier.defaultWeight())
                if (dDay != null) {
                    Text(
                        text = if (dDay > 0) "D-$dDay" else if (dDay == 0) "D-DAY" else "",
                        style = WidgetTextStyle.TitleMediumLight
                    )
                } else {
                    Text("")
                }
            }
        }
    }
}
