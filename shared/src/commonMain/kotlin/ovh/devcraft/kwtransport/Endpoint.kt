package ovh.devcraft.kwtransport

import kotlinx.coroutines.flow.Flow

expect class Endpoint : Closeable {
    suspend fun connect(url: String): Connection
    fun incomingSessions(): Flow<Connection>
    override fun close()
}

expect fun createClientEndpoint(
    certificateHashes: List<String> = emptyList()
): Endpoint
expect fun createServerEndpoint(
    bindAddr: String,
    certificate: Certificate
): Endpoint
