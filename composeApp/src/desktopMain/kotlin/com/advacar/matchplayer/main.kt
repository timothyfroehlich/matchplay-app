package com.advacar.matchplayer

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.advacar.matchplayer.di.commonModule
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.logger.PrintLogger

fun main() = application {
    // Initialize Koin for Desktop
    startKoin {
        logger(PrintLogger(Level.DEBUG)) // Simple logger for desktop
        modules(commonModule) // Add platform-specific modules if needed
    }

    Window(onCloseRequest = ::exitApplication, title = "MatchPlayer") { App() }
}
