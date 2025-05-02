package com.advacar.matchplayer

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "MatchPlayer",
    ) {
        App()
    }
}