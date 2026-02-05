package ovh.devcraft.kwtransport

actual class ConnectionStats actual constructor(
    actual val rtt: Long,
    actual val bytesSent: Long,
    actual val bytesReceived: Long,
    actual val packetsLost: Long,
    actual val congestionWindow: Long?
)