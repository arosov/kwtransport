package ovh.devcraft.kwtransport

data class ConnectionStats(
    val rtt: Long,
    val bytesSent: Long,
    val bytesReceived: Long,
    val packetsLost: Long,
    val congestionWindow: Long?
)
