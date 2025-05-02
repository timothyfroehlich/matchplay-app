package com.advacar.matchplayer

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform