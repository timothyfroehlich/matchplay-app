package com.matchplay.client.network

import com.matchplay.client.auth.ApiKeyStorage
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.dsl.module

// expect fun httpClientEngine(): HttpClientEngine

val networkModule = module {
    single {
        val apiKeyStorage: ApiKeyStorage = get()
        HttpClient(CIO) { // CIO is a good default for JVM/Desktop
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
            defaultRequest {
                // This block is executed for every request
                val apiKey = kotlinx.coroutines.runBlocking { apiKeyStorage.getApiKey() } // Consider a non-blocking way if this causes issues
                if (!apiKey.isNullOrBlank()) {
                    header("X-Api-Key", apiKey) // Or "Authorization: Bearer $apiKey" - check Matchplay API docs
                }
            }
        }
    }
    // Define your ApiService/Repository that uses the HttpClient here
    // single { MatchplayApiService(get()) }
}
