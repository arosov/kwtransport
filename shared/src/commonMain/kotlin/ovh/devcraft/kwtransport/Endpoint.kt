package ovh.devcraft.kwtransport

import kotlinx.coroutines.flow.Flow

expect class Endpoint : Closeable {
    companion object {
        fun createClientEndpoint(): Endpoint
        fun createServerEndpoint(
            bindAddr: String,
            certificate: Certificate
        ): Endpoint
    }
    suspend fun connect(url: String): Connection
    fun incomingSessions(): Flow<Connection>
}