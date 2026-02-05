package ovh.devcraft.kwtransport

actual class ConnectionStats actual constructor(
    actual val rtt: Long,
    actual val bytesSent: Long,
    actual val bytesReceived: Long,
    actual val packetsLost: Long,
    actual val congestionWindow: Long?
) {
    // JNI Constructor matching Rust: (Long, Long, Long, Long)
    internal constructor(rtt: Long, lostPackets: Long, sentPackets: Long, congestionEvents: Long) : 
        this(rtt, sentPackets, 0L, lostPackets, null)
}