package ovh.devcraft.kwtransport

data class QuicConfig(
    val maxConcurrentBiStreams: Long? = null,
    val maxConcurrentUniStreams: Long? = null,
    val initialMaxData: Long? = null,
    val initialMaxStreamDataBidiLocal: Long? = null,
    val initialMaxStreamDataBidiRemote: Long? = null,
    val initialMaxStreamDataUni: Long? = null,
    val datagramReceiveBufferSize: Long? = null,
    val datagramSendBufferSize: Long? = null,
    val congestionController: CongestionController? = null
) {
    internal fun toLongList(): List<Long> {
        return listOf(
            maxConcurrentBiStreams ?: -1L,
            maxConcurrentUniStreams ?: -1L,
            initialMaxData ?: -1L,
            initialMaxStreamDataBidiLocal ?: -1L,
            initialMaxStreamDataBidiRemote ?: -1L,
            initialMaxStreamDataUni ?: -1L,
            datagramReceiveBufferSize ?: -1L,
            datagramSendBufferSize ?: -1L,
            congestionController?.ordinal?.toLong() ?: -1L
        )
    }
}
