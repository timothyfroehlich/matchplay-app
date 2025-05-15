package com.matchplay.client.auth

import java.util.prefs.Preferences

actual class ApiKeyStorage {
    private val prefs = Preferences.userRoot().node("com.matchplay.client")
    private val apiKeyName = "matchplay_api_key"

    actual suspend fun saveApiKey(key: String) {
        prefs.put(apiKeyName, key)
    }

    actual suspend fun getApiKey(): String? {
        return prefs.get(apiKeyName, null)
    }

    actual suspend fun clearApiKey() {
        prefs.remove(apiKeyName)
    }
}
