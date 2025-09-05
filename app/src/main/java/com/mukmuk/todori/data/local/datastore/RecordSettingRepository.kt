package com.mukmuk.todori.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import javax.inject.Named

@Singleton
class RecordSettingRepository @Inject constructor(
    @Named("record_settings") private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val TOTAL_RECORD_MILLS_KEY = longPreferencesKey("total_record_time_mills")
        private val IS_RUNNING = booleanPreferencesKey("is_running")
    }

    val totalRecordTimeFlow: Flow<Long> = dataStore.data.map { prefs ->
        prefs[TOTAL_RECORD_MILLS_KEY] ?: 0L
    }
    val runningStateFlow: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[IS_RUNNING] ?: false
    }

    suspend fun saveTotalRecordTime(time: Long) {
        dataStore.edit { prefs ->
            prefs[TOTAL_RECORD_MILLS_KEY] = time
        }
    }

    suspend fun saveRunningState(isRunning: Boolean) {
        dataStore.edit { prefs ->
            prefs[IS_RUNNING] = isRunning
        }
    }

    suspend fun clearRecordSetting() {
        dataStore.edit { it.clear() }
    }
}
