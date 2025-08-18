package com.mukmuk.todori.widget.timer

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.mukmuk.todori.data.local.datastore.RecordSettingRepository
import kotlinx.coroutines.flow.first

class TimerAction(
    private val repository: RecordSettingRepository
) : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val current = repository.runningStateFlow.first()
        // 반전 값 저장
        repository.saveRunningState(!current)

        // 위젯 다시 업데이트
        TimerWidget().update(context, glanceId)
    }
}