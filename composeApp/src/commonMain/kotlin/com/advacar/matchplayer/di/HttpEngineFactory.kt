package com.advacar.matchplayer.di

import io.ktor.client.engine.* // HttpClientEngineFactory is here

/**
 * Expected function to provide a platform-specific Ktor HttpClientEngineFactory.
 */
expect fun httpClientEngineFactory(): HttpClientEngineFactory<*>
