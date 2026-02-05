package io.github.arosov.kwtransport

data class StreamPair(
    val send: SendStream,
    val recv: RecvStream
)
