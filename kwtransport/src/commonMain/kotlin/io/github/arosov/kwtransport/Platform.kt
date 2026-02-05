package io.github.arosov.kwtransport

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform