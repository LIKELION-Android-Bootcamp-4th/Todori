package com.mukmuk.todori.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.mukmuk.todori.ui.screen.home.home_setting.HomeSettingState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// 파일 최상단에 DataStore 인스턴스를 정의합니다.
// 'home_settings'는 DataStore 파일의 이름입니다.
private val Context.homeSettingDataStore: DataStore<Preferences> by preferencesDataStore(name = "home_settings")

class HomeSettingRepository(private val context: Context) {

    // DataStore에서 설정을 읽어와 HomeSettingState Flow로 변환합니다.
    val homeSettingStateFlow: Flow<HomeSettingState> = context.homeSettingDataStore.data
        .map { preferences ->
            HomeSettingState(
                isPomodoroEnabled = preferences[HomeSettingPreferencesKeys.IS_POMODORO_ENABLED] ?: true,
                focusMinutes = preferences[HomeSettingPreferencesKeys.FOCUS_MINUTES] ?: 25,
                focusSeconds = preferences[HomeSettingPreferencesKeys.FOCUS_SECONDS] ?: 0,
                shortRestMinutes = preferences[HomeSettingPreferencesKeys.SHORT_REST_MINUTES] ?: 5,
                shortRestSeconds = preferences[HomeSettingPreferencesKeys.SHORT_REST_SECONDS] ?: 0,
                longRestMinutes = preferences[HomeSettingPreferencesKeys.LONG_REST_MINUTES] ?: 15,
                longRestSeconds = preferences[HomeSettingPreferencesKeys.LONG_REST_SECONDS] ?: 0
            )
        }

    // HomeSettingState를 DataStore에 저장합니다.
    suspend fun saveHomeSettingState(homeSettingState: HomeSettingState) {
        context.homeSettingDataStore.edit { preferences ->
            preferences[HomeSettingPreferencesKeys.IS_POMODORO_ENABLED] = homeSettingState.isPomodoroEnabled
            preferences[HomeSettingPreferencesKeys.FOCUS_MINUTES] = homeSettingState.focusMinutes
            preferences[HomeSettingPreferencesKeys.FOCUS_SECONDS] = homeSettingState.focusSeconds
            preferences[HomeSettingPreferencesKeys.SHORT_REST_MINUTES] = homeSettingState.shortRestMinutes
            preferences[HomeSettingPreferencesKeys.SHORT_REST_SECONDS] = homeSettingState.shortRestSeconds
            preferences[HomeSettingPreferencesKeys.LONG_REST_MINUTES] = homeSettingState.longRestMinutes
            preferences[HomeSettingPreferencesKeys.LONG_REST_SECONDS] = homeSettingState.longRestSeconds
        }
    }
}