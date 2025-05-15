package com.matchplay.client.auth

expect class ApiKeyStorage {
    suspend fun saveApiKey(key: String)
    suspend fun getApiKey(): String?
    suspend fun clearApiKey()
}
