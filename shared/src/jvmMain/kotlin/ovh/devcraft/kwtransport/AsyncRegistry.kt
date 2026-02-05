package ovh.devcraft.kwtransport

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.SendChannel
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import ovh.devcraft.kwtransport.exceptions.*

internal object AsyncRegistry {
    private val idGenerator = AtomicLong(1)
    private val pendingOps = ConcurrentHashMap<Long, Any>()

    fun createDeferred(): Pair<Long, CompletableDeferred<Long>> {
        val id = idGenerator.getAndIncrement()
        val deferred = CompletableDeferred<Long>()
        pendingOps[id] = deferred
        return id to deferred
    }

    fun registerChannel(channel: SendChannel<Long>): Long {
        val id = idGenerator.getAndIncrement()
        pendingOps[id] = channel
        return id
    }

    fun remove(id: Long) {
        pendingOps.remove(id)
    }

    fun resolve(id: Long, result: Long, errorType: String, errorMessage: String) {
        val entry = pendingOps[id] ?: return
        
        if (entry is CompletableDeferred<*>) {
            pendingOps.remove(id)
            @Suppress("UNCHECKED_CAST")
            val deferred = entry as CompletableDeferred<Long>
            
            if (errorType.isNotEmpty()) {
                val exception = mapException(errorType, errorMessage)
                deferred.completeExceptionally(exception)
            } else {
                deferred.complete(result)
            }
        } else if (entry is SendChannel<*>) {
            @Suppress("UNCHECKED_CAST")
            val channel = entry as SendChannel<Long>
            
            if (errorType.isEmpty()) {
                channel.trySend(result)
            } else {
                // For flows, we might want to close the channel with an error or just log it
                channel.close(mapException(errorType, errorMessage))
                pendingOps.remove(id)
            }
        }
    }

    private fun mapException(errorType: String, errorMessage: String): KwTransportException {
        return when (errorType) {
            "CONNECTING" -> ConnectingException(errorMessage, ConnectingErrorType.CONNECTION_ERROR)
            "CONNECTION" -> ConnectionException(errorMessage, ConnectionErrorType.QUIC_PROTO)
            "INVALID_URL" -> ConnectingException(errorMessage, ConnectingErrorType.INVALID_URL)
            "DNS_LOOKUP" -> ConnectingException(errorMessage, ConnectingErrorType.DNS_LOOKUP)
            "DNS_NOT_FOUND" -> ConnectingException(errorMessage, ConnectingErrorType.DNS_NOT_FOUND)
            "SESSION_REJECTED" -> ConnectingException(errorMessage, ConnectingErrorType.SESSION_REJECTED)
            "RESERVED_HEADER" -> ConnectingException(errorMessage, ConnectingErrorType.RESERVED_HEADER)
            "ENDPOINT_STOPPING" -> ConnectingException(errorMessage, ConnectingErrorType.ENDPOINT_STOPPING)
            "CIDS_EHAUSTED" -> ConnectingException(errorMessage, ConnectingErrorType.CIDS_EXHAUSTED)
            "INVALID_SERVER_NAME" -> ConnectingException(errorMessage, ConnectingErrorType.INVALID_SERVER_NAME)
            "INVALID_REMOTE_ADDRESS" -> ConnectingException(errorMessage, ConnectingErrorType.INVALID_REMOTE_ADDRESS)
            else -> KwTransportException(errorMessage)
        }
    }
}
