package com.mukmuk.todori.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import javax.inject.Named

@Singleton
class RecordSettingRepository @Inject constructor(
    @Named("record_settings") private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val TOTAL_RECORD_KEY = longPreferencesKey("total_record_time_mills")
    }

    val totalRecordTimeFlow: Flow<Long> = dataStore.data.map { prefs ->
        prefs[TOTAL_RECORD_KEY] ?: 0L
    }

    suspend fun saveTotalRecordTime(time: Long) {
        dataStore.edit { prefs ->
            prefs[TOTAL_RECORD_KEY] = time
        }
    }

    suspend fun getTotalTime(): String {
        val millis = dataStore.data
            .map { prefs -> prefs[TOTAL_RECORD_KEY] ?: 0L }
            .first()
        
        val h = (millis / 1000) / 3600
        val m = (millis / 1000 % 3600) / 60
        val s = (millis / 1000) % 60
        return String.format("%02d:%02d:%02d", h, m, s)
    }

    suspend fun clearTotalRecordTime() {
        dataStore.edit { prefs ->
            prefs.remove(TOTAL_RECORD_KEY)
        }
    }
}
