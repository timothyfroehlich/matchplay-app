package com.matchplay.api

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlin.test.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Tag

@Tag("integration")
class LiveApiTest {
    private lateinit var client: MatchplayClient

    @BeforeTest
    fun setup() {
        val apiKey = System.getProperty("matchplay.apiKey")
            ?: throw IllegalStateException(
                "API key not found. Create local.properties with matchplay.apiKey"
            )

        val httpClient = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                })
            }
        }

        client = MatchplayClient(
            httpClient = httpClient,
            apiKey = apiKey
        )
    }

    @AfterTest
    fun teardown() {
        client.close()
    }

    @Test
    fun `can list tournaments`() = runBlocking {
        val tournaments = client.getTournaments()
        assertNotNull(tournaments)
        // Add more specific assertions based on expected data
    }

    // Add more integration tests for other endpoints as needed
}
