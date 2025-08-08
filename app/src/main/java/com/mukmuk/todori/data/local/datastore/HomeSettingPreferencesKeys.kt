package com.mukmuk.todori.data.local.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey

object HomeSettingPreferencesKeys {
    val IS_POMODORO_ENABLED = booleanPreferencesKey("is_pomodoro_enabled")
    val FOCUS_MINUTES = intPreferencesKey("focus_minutes")
    val FOCUS_SECONDS = intPreferencesKey("focus_seconds")
    val SHORT_REST_MINUTES = intPreferencesKey("short_rest_minutes")
    val SHORT_REST_SECONDS = intPreferencesKey("short_rest_seconds")
    val LONG_REST_MINUTES = intPreferencesKey("long_rest_minutes")
    val LONG_REST_SECONDS = intPreferencesKey("long_rest_seconds")
}