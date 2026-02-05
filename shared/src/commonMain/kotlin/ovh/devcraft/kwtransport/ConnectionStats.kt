package ovh.devcraft.kwtransport

expect class ConnectionStats(
    rtt: Long,
    bytesSent: Long,
    bytesReceived: Long,
    packetsLost: Long,
    congestionWindow: Long?
) {
    val rtt: Long
    val bytesSent: Long
    val bytesReceived: Long
    val packetsLost: Long
    val congestionWindow: Long?
}
