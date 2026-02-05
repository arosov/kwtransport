package ovh.devcraft.kwtransport

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map

class Endpoint internal constructor(private var handle: Long) : AutoCloseable {
    companion object {
        init {
            KwTransport
        }

        @JvmStatic
        private external fun createClient(
            bindAddr: String, 
            acceptAllCerts: Boolean, 
            maxIdleTimeoutMillis: Long
        ): Long

        @JvmStatic
        private external fun createServer(bindAddr: String, certHandle: Long): Long
        
        @JvmStatic
        private external fun destroy(handle: Long)

        @JvmStatic
        private external fun connect(handle: Long, id: Long, url: String)

        @JvmStatic
        private external fun listenSessions(handle: Long, id: Long)

        @JvmStatic
        private external fun stopListenSessions(handle: Long)

        fun createClientEndpoint(
            bindAddr: String = "127.0.0.1:0", 
            acceptAllCerts: Boolean = false,
            maxIdleTimeoutMillis: Long = 30000L
        ): Endpoint {
            val handle = createClient(bindAddr, acceptAllCerts, maxIdleTimeoutMillis)
            return Endpoint(handle)
        }

        fun createServerEndpoint(bindAddr: String, certificate: Certificate): Endpoint {
            val handle = createServer(bindAddr, certificate.handle)
            certificate.handle = 0L 
            return Endpoint(handle)
        }
    }

    suspend fun connect(url: String): Connection {
        if (handle == 0L) throw IllegalStateException("Endpoint is closed")
        
        val (id, deferred) = AsyncRegistry.createDeferred()
        connect(handle, id, url)
        
        val connHandle = deferred.await()
        return Connection(connHandle)
    }

    fun incomingSessions(): Flow<Connection> = callbackFlow<Long> {
        if (handle == 0L) throw IllegalStateException("Endpoint is closed")
        
        val id = AsyncRegistry.registerChannel(channel)
        listenSessions(handle, id)
        
        awaitClose {
            stopListenSessions(handle)
            AsyncRegistry.remove(id)
        }
    }.map { handle -> Connection(handle) }

    override fun close() {
        if (handle != 0L) {
            destroy(handle)
            handle = 0L
        }
    }
    
    fun isClosed(): Boolean = handle == 0L
}
