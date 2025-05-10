package com.advacar.matchplayer

import android.app.Application
import com.advacar.matchplayer.di.commonModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MatchPlayerApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            // Log Koin activity - use Level.INFO for production or Level.DEBUG for debugging
            androidLogger(Level.DEBUG)
            // Declare Android context
            androidContext(this@MatchPlayerApplication)
            // Declare modules to use
            modules(commonModule) // Add other platform-specific modules if needed
        }
    }
}
