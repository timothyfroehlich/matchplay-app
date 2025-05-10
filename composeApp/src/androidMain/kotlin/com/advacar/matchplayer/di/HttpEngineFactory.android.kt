package com.advacar.matchplayer.di

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.okhttp.OkHttp

/**
 * Actual implementation of HttpClientEngineFactory for Android, using OkHttp.
 */
actual fun httpClientEngineFactory(): HttpClientEngineFactory<*> {
    return OkHttp // Corrected: return the factory itself, not an instance from create()
}
