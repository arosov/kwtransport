package ovh.devcraft.kwtransport

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import java.util.concurrent.atomic.AtomicLong

class Endpoint internal constructor(handle: Long) : AutoCloseable {
    private val handle = AtomicLong(handle)
    companion object {
        init {
            KwTransport
        }

        @JvmStatic
        private external fun createClient(
            bindAddr: String, 
            acceptAllCerts: Boolean, 
            maxIdleTimeoutMillis: Long,
            certificateHashes: List<String>,
            keepAliveIntervalMillis: Long,
            ipv6DualStackConfig: Int,
            quicConfig: List<Long>
        ): Long

        @JvmStatic
        private external fun createServer(
            bindAddr: String, 
            certHandle: Long,
            maxIdleTimeoutMillis: Long,
            keepAliveIntervalMillis: Long,
            allowMigration: Boolean,
            ipv6DualStackConfig: Int,
            quicConfig: List<Long>
        ): Long
        
        @JvmStatic
        private external fun destroy(handle: Long)

        @JvmStatic
        private external fun connect(handle: Long, id: Long, url: String)

        @JvmStatic
        private external fun listenSessions(handle: Long, id: Long)

        @JvmStatic
        private external fun stopListenSessions(handle: Long)

        @JvmStatic
        private external fun getLocalAddr(handle: Long): String

        // IPv6 Dual Stack Config Constants
        const val IPV6_DUAL_STACK_DEFAULT = 0
        const val IPV6_DUAL_STACK_ALLOW = 1
        const val IPV6_DUAL_STACK_DENY = 2

        fun createClientEndpoint(
            bindAddr: String = "127.0.0.1:0", 
            acceptAllCerts: Boolean = false,
            maxIdleTimeoutMillis: Long = 30000L,
            certificateHashes: List<String>? = null,
            keepAliveIntervalMillis: Long = 0L,
            ipv6DualStackConfig: Int = IPV6_DUAL_STACK_DEFAULT,
            quicConfig: QuicConfig? = null
        ): Endpoint {
            val handle = createClient(
                bindAddr, 
                acceptAllCerts, 
                maxIdleTimeoutMillis, 
                certificateHashes ?: emptyList(),
                keepAliveIntervalMillis,
                ipv6DualStackConfig,
                quicConfig?.toLongList() ?: emptyList()
            )
            return Endpoint(handle)
        }

        fun createServerEndpoint(
            bindAddr: String, 
            certificate: Certificate,
            maxIdleTimeoutMillis: Long = 30000L,
            keepAliveIntervalMillis: Long = 0L,
            allowMigration: Boolean = true,
            ipv6DualStackConfig: Int = IPV6_DUAL_STACK_DEFAULT,
            quicConfig: QuicConfig? = null
        ): Endpoint {
            val certHandle = certificate.handle.getAndSet(0L)
            if (certHandle == 0L) throw IllegalStateException("Certificate is already used or closed")
            val handle = createServer(
                bindAddr, 
                certHandle, 
                maxIdleTimeoutMillis, 
                keepAliveIntervalMillis, 
                allowMigration,
                ipv6DualStackConfig,
                quicConfig?.toLongList() ?: emptyList()
            )
            return Endpoint(handle)
        }
    }

    val localAddr: String
        get() {
            val h = handle.get()
            if (h == 0L) throw IllegalStateException("Endpoint is closed")
            return getLocalAddr(h)
        }

    suspend fun connect(url: String): Connection {
        val h = handle.get()
        if (h == 0L) throw IllegalStateException("Endpoint is closed")
        
        val (id, deferred) = AsyncRegistry.createDeferred()
        connect(h, id, url)
        
        val connHandle = deferred.await()
        return Connection(connHandle)
    }

    fun incomingSessions(): Flow<Connection> = callbackFlow<Long> {
        val h = handle.get()
        if (h == 0L) throw IllegalStateException("Endpoint is closed")
        
        val id = AsyncRegistry.registerChannel(channel)
        listenSessions(h, id)
        
        awaitClose {
            val currentH = handle.get()
            if (currentH != 0L) {
                stopListenSessions(currentH)
            }
            AsyncRegistry.remove(id)
        }
    }.map { handle -> Connection(handle) }

    override fun close() {
        val h = handle.getAndSet(0L)
        if (h != 0L) {
            destroy(h)
        }
    }
    
    fun isClosed(): Boolean = handle.get() == 0L
}
