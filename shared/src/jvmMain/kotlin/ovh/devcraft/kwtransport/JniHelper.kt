package ovh.devcraft.kwtransport

import ovh.devcraft.kwtransport.exceptions.*

object JniHelper {
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
