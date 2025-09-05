package com.mukmuk.todori.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.mukmuk.todori.ui.screen.home.home_setting.HomeSettingState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class HomeSettingRepository @Inject constructor(
    @Named("home_settings") private val dataStore: DataStore<Preferences>
) {

    val homeSettingStateFlow: Flow<HomeSettingState> = dataStore.data
        .map { preferences ->
            HomeSettingState(
                isPomodoroEnabled = preferences[HomeSettingPreferencesKeys.IS_POMODORO_ENABLED]
                    ?: true,
                focusMinutes = preferences[HomeSettingPreferencesKeys.FOCUS_MINUTES] ?: 25,
                focusSeconds = preferences[HomeSettingPreferencesKeys.FOCUS_SECONDS] ?: 0,
                shortRestMinutes = preferences[HomeSettingPreferencesKeys.SHORT_REST_MINUTES] ?: 5,
                shortRestSeconds = preferences[HomeSettingPreferencesKeys.SHORT_REST_SECONDS] ?: 0,
                longRestMinutes = preferences[HomeSettingPreferencesKeys.LONG_REST_MINUTES] ?: 15,
                longRestSeconds = preferences[HomeSettingPreferencesKeys.LONG_REST_SECONDS] ?: 0
            )
        }

    suspend fun saveHomeSettingState(homeSettingState: HomeSettingState) {
        dataStore.edit { preferences ->
            preferences[HomeSettingPreferencesKeys.IS_POMODORO_ENABLED] =
                homeSettingState.isPomodoroEnabled
            preferences[HomeSettingPreferencesKeys.FOCUS_MINUTES] = homeSettingState.focusMinutes
            preferences[HomeSettingPreferencesKeys.FOCUS_SECONDS] = homeSettingState.focusSeconds
            preferences[HomeSettingPreferencesKeys.SHORT_REST_MINUTES] =
                homeSettingState.shortRestMinutes
            preferences[HomeSettingPreferencesKeys.SHORT_REST_SECONDS] =
                homeSettingState.shortRestSeconds
            preferences[HomeSettingPreferencesKeys.LONG_REST_MINUTES] =
                homeSettingState.longRestMinutes
            preferences[HomeSettingPreferencesKeys.LONG_REST_SECONDS] =
                homeSettingState.longRestSeconds
        }
    }

    suspend fun clearHomeSetting() {
        dataStore.edit { it.clear() }
    }
}
