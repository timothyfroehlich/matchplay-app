package com.matchplay.client.auth

import kotlinx.coroutines.runBlocking
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

// This test will run on each platform (JVM, Android)
// For Android, it requires a Context, which needs to be provided by a platform-specific test runner setup.
// For Desktop, it runs directly.

expect fun getTestApiKeyStorage(): ApiKeyStorage

class ApiKeyStorageTest {

    private lateinit var apiKeyStorage: ApiKeyStorage

    @BeforeTest
    fun setUp() {
        apiKeyStorage = getTestApiKeyStorage()
        // Ensure a clean state before each test if necessary
        runBlocking { apiKeyStorage.clearApiKey() }
    }

    @AfterTest
    fun tearDown() {
        // Clean up after tests if necessary
        runBlocking { apiKeyStorage.clearApiKey() }
    }

    @Test
    fun testSaveAndGetApiKey() = runBlocking {
        val testKey = "test_api_key_123"
        apiKeyStorage.saveApiKey(testKey)
        assertEquals(testKey, apiKeyStorage.getApiKey())
    }

    @Test
    fun testGetApiKeyWhenNoneSet() = runBlocking {
        assertNull(apiKeyStorage.getApiKey())
    }

    @Test
    fun testClearApiKey() = runBlocking {
        val testKey = "test_api_key_to_clear"
        apiKeyStorage.saveApiKey(testKey)
        assertEquals(testKey, apiKeyStorage.getApiKey()) // Ensure it was saved
        apiKeyStorage.clearApiKey()
        assertNull(apiKeyStorage.getApiKey())
    }

    @Test
    fun testOverwriteApiKey() = runBlocking {
        val firstKey = "first_key"
        val secondKey = "second_key"

        apiKeyStorage.saveApiKey(firstKey)
        assertEquals(firstKey, apiKeyStorage.getApiKey())

        apiKeyStorage.saveApiKey(secondKey)
        assertEquals(secondKey, apiKeyStorage.getApiKey())
    }
}
