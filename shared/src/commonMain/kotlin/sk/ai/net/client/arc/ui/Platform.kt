package sk.ai.net.client.arc.ui

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform