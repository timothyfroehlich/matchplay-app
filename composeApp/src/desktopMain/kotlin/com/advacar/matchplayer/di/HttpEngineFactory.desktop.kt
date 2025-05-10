package com.advacar.matchplayer.di

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.java.Java

/**
 * Actual implementation of HttpClientEngineFactory for Desktop, using Java's default engine.
 * CIO could also be used here if preferred: import io.ktor.client.engine.cio.CIO
 */
actual fun httpClientEngineFactory(): HttpClientEngineFactory<*> {
    return Java
    // Alternatively, for CIO engine:
    // return CIO.create()
}
