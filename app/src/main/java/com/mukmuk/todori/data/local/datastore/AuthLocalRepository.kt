package com.mukmuk.todori.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class AuthLocalRepository@Inject constructor(
    @Named("user_pref") private val dataStore: DataStore<Preferences>
) {
    private val LAST_LOGIN_PROVIDER_KEY = stringPreferencesKey("last_login_provider")

    suspend fun saveLastLoginProvider(provider: String) {
        dataStore.edit { prefs ->
            prefs[LAST_LOGIN_PROVIDER_KEY] = provider
        }
    }

    fun getLastLoginProvider(): Flow<String?> {
        return dataStore.data.map { prefs ->
            prefs[LAST_LOGIN_PROVIDER_KEY]
        }
    }
}