package com.konkuk.mocacong.util

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object TokenManager {
    private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
    private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")

    private lateinit var dataStore: DataStore<Preferences>

    fun setDataStore(dataStore: DataStore<Preferences>) {
        this.dataStore = dataStore
    }

    fun getAccessToken(): Flow<String?> {
        return dataStore.data.map { pref ->
            pref[ACCESS_TOKEN_KEY]
        }
    }

    fun getRefreshToken(): Flow<String?> {
        return dataStore.data.map { pref ->
            pref[REFRESH_TOKEN_KEY]
        }
    }

    suspend fun saveAccessToken(token: String) {
        dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN_KEY] = token
        }
    }

    suspend fun saveRefreshToken(token: String) {
        dataStore.edit { prefs ->
            prefs[REFRESH_TOKEN_KEY] = token
        }
    }

    suspend fun deleteAccessToken() {
        dataStore.edit { prefs ->
            prefs.remove(ACCESS_TOKEN_KEY)
        }
    }
}

