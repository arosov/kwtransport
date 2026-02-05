package ovh.devcraft.kwtransport

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
        private external fun connect(handle: Long, url: String): Long

        @JvmStatic
        private external fun acceptSession(handle: Long): Long

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

    fun connect(url: String): Connection {
        if (handle == 0L) throw IllegalStateException("Endpoint is closed")
        val connHandle = connect(handle, url)
        return Connection(connHandle)
    }

    fun acceptSession(): Connection {
        if (handle == 0L) throw IllegalStateException("Endpoint is closed")
        val connHandle = acceptSession(handle)
        return Connection(connHandle)
    }

    override fun close() {
        if (handle != 0L) {
            destroy(handle)
            handle = 0L
        }
    }
    
    fun isClosed(): Boolean = handle == 0L
}
