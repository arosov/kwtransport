package ovh.devcraft.kwtransport

data class ConnectionStats(
    val rttMs: Long,
    val lostPackets: Long,
    val sentPackets: Long,
    val congestionEvents: Long
)
