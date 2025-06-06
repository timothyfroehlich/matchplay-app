package com.advacar.matchplayer.di

import com.advacar.matchplayer.data.remote.MatchplayApiService
import com.advacar.matchplayer.data.remote.MatchplayApiServiceImpl
import com.advacar.matchplayer.data.repository.TournamentRepository
import com.advacar.matchplayer.data.repository.TournamentRepositoryImpl
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val networkModule = module {
    single<HttpClient> {
        HttpClient(httpClientEngineFactory()) {
            install(ContentNegotiation) {
                json(
                    Json {
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                    }
                )
            }

            install(Logging) { // Logging feature installation
                logger = Logger.DEFAULT // Use Ktor's default logger
                level = LogLevel.ALL // Log all messages
            }

            // Default request configuration can be set here if needed
        }
    }

    single<MatchplayApiService> {
        MatchplayApiServiceImpl(get()) // Koin will inject the HttpClient
    }
}

val repositoryModule = module {
    single<TournamentRepository> {
        TournamentRepositoryImpl(get())
    } // Koin will inject MatchplayApiService
}

// Combined app modules for platform entry points
val commonAppModules = listOf(networkModule, repositoryModule)
