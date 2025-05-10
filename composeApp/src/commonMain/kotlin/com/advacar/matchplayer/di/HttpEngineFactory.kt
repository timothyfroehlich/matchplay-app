package com.advacar.matchplayer.di

import io.ktor.client.engine.HttpClientEngineFactory

/** Expected function to provide a platform-specific Ktor HttpClientEngineFactory. */
expect fun httpClientEngineFactory(): HttpClientEngineFactory<*>
