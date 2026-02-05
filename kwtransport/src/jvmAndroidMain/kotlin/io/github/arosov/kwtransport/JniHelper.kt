package io.github.arosov.kwtransport

import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import io.github.arosov.kwtransport.exceptions.*
import java.net.InetAddress

object JniHelper {

    private val scope = CoroutineScope(SupervisorJob())

    @JvmStatic
    fun onNotify(id: Long, result: Long, errorType: String, errorMessage: String, errorCode: Long, errorContext: String) {
        AsyncRegistry.resolve(id, result, errorType, errorMessage, errorCode, errorContext)
    }

    @JvmStatic
    private external fun resolveDnsResponse(requestId: Long, ipAddress: String?, errorMessage: String?)

    @JvmStatic
    fun onResolveRequest(resolverId: Long, host: String, requestId: Long) {
        scope.launch {
            val resolver = AsyncRegistry.getResolverObject(resolverId)
            if (resolver == null) {
                resolveDnsResponse(requestId, null, "DNS resolver not found for ID: $resolverId")
                return@launch
            }

            try {
                val ipAddresses = resolver.resolve(host)
                val ipAddress = ipAddresses.firstOrNull()?.hostAddress // Take the first resolved IP
                resolveDnsResponse(requestId, ipAddress, null)
            } catch (e: Exception) {
                resolveDnsResponse(requestId, null, e.message)
            }
        }
    }

    @JvmStatic
    fun throwConnectingException(message: String, typeName: String) {
        val type = try {
            ConnectingErrorType.valueOf(typeName)
        } catch (e: IllegalArgumentException) {
            ConnectingErrorType.CONNECTION_ERROR
        }
        throw ConnectingException(message, type)
    }

    @JvmStatic
    fun throwConnectionException(message: String, typeName: String) {
        val type = try {
            ConnectionErrorType.valueOf(typeName)
        } catch (e: IllegalArgumentException) {
            ConnectionErrorType.QUIC_PROTO
        }
        throw ConnectionException(message, type)
    }

    @JvmStatic
    fun throwStreamOpeningException(message: String, typeName: String) {
        val type = try {
            StreamOpeningErrorType.valueOf(typeName)
        } catch (e: IllegalArgumentException) {
            StreamOpeningErrorType.NOT_CONNECTED
        }
        throw StreamOpeningException(message, type)
    }

    @JvmStatic
    fun throwSendDatagramException(message: String, typeName: String) {
        val type = try {
            SendDatagramErrorType.valueOf(typeName)
        } catch (e: IllegalArgumentException) {
            SendDatagramErrorType.NOT_CONNECTED
        }
        throw SendDatagramException(message, type)
    }
}
