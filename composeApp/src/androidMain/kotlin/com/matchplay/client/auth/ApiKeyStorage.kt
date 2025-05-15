package com.matchplay.client.auth

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// At the top level of your kotlin file:
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "secure_settings")

actual class ApiKeyStorage(private val context: Context) {
    private val apiKeyName = "matchplay_api_key"
    private val API_KEY = stringPreferencesKey(apiKeyName)

    actual suspend fun saveApiKey(key: String) {
        context.dataStore.edit {
            it[API_KEY] = key
        }
    }

    actual suspend fun getApiKey(): String? {
        return context.dataStore.data.map {
            it[API_KEY]
        }.first()
    }

    actual suspend fun clearApiKey() {
        context.dataStore.edit {
            it.remove(API_KEY)
        }
    }
}
