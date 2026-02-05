package ovh.devcraft.kwtransport

data class StreamPair(
    val send: SendStream,
    val recv: RecvStream
)
