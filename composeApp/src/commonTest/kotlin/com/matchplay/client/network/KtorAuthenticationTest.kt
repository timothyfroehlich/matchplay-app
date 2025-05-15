package com.matchplay.client.network

import com.matchplay.client.auth.ApiKeyStorage
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.*

// Mock ApiKeyStorage for tests
class MockApiKeyStorage : ApiKeyStorage {
    private var apiKey: String? = null
    override suspend fun saveApiKey(key: String) {
        apiKey = key
    }

    override suspend fun getApiKey(): String? {
        return apiKey
    }

    override suspend fun clearApiKey() {
        apiKey = null
    }
}

class KtorAuthenticationTest : KoinTest {

    private val mockApiKeyStorage = MockApiKeyStorage()

    @BeforeTest
    fun setUp() {
        startKoin {
            modules(
                module {
                    single<ApiKeyStorage> { mockApiKeyStorage }
                    single {
                        HttpClient(MockEngine) {
                            engine {
                                addHandler {
                                    // Default handler, respond with OK and check headers
                                    respond("", HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()))
                                }
                            }
                            install(ContentNegotiation) {
                                json(Json { ignoreUnknownKeys = true })
                            }
                            defaultRequest {
                                val key = mockApiKeyStorage.getApiKey()
                                if (!key.isNullOrBlank()) {
                                    header("X-Api-Key", key)
                                }
                            }
                        }
                    }
                }
            )
        }
    }

    @AfterTest
    fun tearDown() {
        stopKoin()
        runBlocking { mockApiKeyStorage.clearApiKey() } // Clear the key for the next test
    }

    private val client: HttpClient by inject()

    @Test
    fun testRequestWithoutApiKey() = runBlocking {
        mockApiKeyStorage.clearApiKey() // Ensure no key is set

        val response = client.get("http://localhost/test")
        assertEquals(HttpStatusCode.OK, response.status)
        assertNull(response.request.headers["X-Api-Key"], "X-Api-Key header should not be present")
    }

    @Test
    fun testRequestWithApiKey() = runBlocking {
        val testKey = "my-secret-api-key"
        mockApiKeyStorage.saveApiKey(testKey)

        val response = client.get("http://localhost/test")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(testKey, response.request.headers["X-Api-Key"], "X-Api-Key header is missing or incorrect")
    }

    @Test
    fun testRequestWithBlankApiKey() = runBlocking {
        mockApiKeyStorage.saveApiKey("") // Set a blank key

        val response = client.get("http://localhost/test")
        assertEquals(HttpStatusCode.OK, response.status)
        assertNull(response.request.headers["X-Api-Key"], "X-Api-Key header should not be present for blank key")
    }
}
