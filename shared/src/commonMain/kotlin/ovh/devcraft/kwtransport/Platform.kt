package ovh.devcraft.kwtransport

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform