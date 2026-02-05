package ovh.devcraft.kwtransport.exceptions

/**
 * Base class for all kwtransport exceptions.
 */
open class KwTransportException(message: String? = null, cause: Throwable? = null) : Exception(message, cause)

/**
 * Exception thrown when a connection error occurs.
 */
class ConnectionException(
    message: String,
    val type: ConnectionErrorType,
    val errorCode: Long? = null,
    val reason: String? = null
) : KwTransportException(message)

enum class ConnectionErrorType {
    CONNECTION_CLOSED,
    APPLICATION_CLOSED,
    LOCALLY_CLOSED,
    LOCAL_H3_ERROR,
    TIMED_OUT,
    QUIC_PROTO,
    CIDS_EXHAUSTED
}

/**
 * Exception thrown when an error occurs during the connection process.
 */
class ConnectingException(
    message: String,
    val type: ConnectingErrorType
) : KwTransportException(message)

enum class ConnectingErrorType {
    INVALID_URL,
    DNS_LOOKUP,
    DNS_NOT_FOUND,
    CONNECTION_ERROR,
    SESSION_REJECTED,
    RESERVED_HEADER,
    ENDPOINT_STOPPING,
    CIDS_EXHAUSTED,
    INVALID_SERVER_NAME,
    INVALID_REMOTE_ADDRESS
}

/**
 * Base class for stream-related exceptions.
 */
open class StreamException(message: String) : KwTransportException(message)

/**
 * Exception thrown when a read error occurs on a stream.
 */
class StreamReadException(
    message: String,
    val type: StreamReadErrorType,
    val errorCode: Long? = null
) : StreamException(message)

enum class StreamReadErrorType {
    NOT_CONNECTED,
    RESET,
    QUIC_PROTO,
    FINISHED_EARLY
}

/**
 * Exception thrown when a write error occurs on a stream.
 */
class StreamWriteException(
    message: String,
    val type: StreamWriteErrorType,
    val errorCode: Long? = null
) : StreamException(message)

enum class StreamWriteErrorType {
    NOT_CONNECTED,
    CLOSED,
    STOPPED,
    QUIC_PROTO
}

/**
 * Exception thrown when an error occurs while opening a stream.
 */
class StreamOpeningException(
    message: String,
    val type: StreamOpeningErrorType
) : StreamException(message)

enum class StreamOpeningErrorType {
    NOT_CONNECTED,
    REFUSED
}

/**
 * Exception thrown when a datagram send error occurs.
 */
class SendDatagramException(
    message: String,
    val type: SendDatagramErrorType
) : KwTransportException(message)

enum class SendDatagramErrorType {
    NOT_CONNECTED,
    UNSUPPORTED_BY_PEER,
    TOO_LARGE
}
