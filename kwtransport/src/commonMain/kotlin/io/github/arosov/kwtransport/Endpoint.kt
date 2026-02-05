package io.github.arosov.kwtransport

import kotlinx.coroutines.flow.Flow

/**
 * Represents a WebTransport endpoint, which can be either a client or a server.
 * Use [createClientEndpoint] or [createServerEndpoint] to instantiate.
 */
expect class Endpoint : Closeable {
    /**
     * Establishes a new WebTransport connection to the specified [url].
     * @param url The WebTransport URL to connect to (e.g., "https://example.com:4433/webtransport").
     * @return A [Connection] once the handshake is complete.
     */
    suspend fun connect(url: String): Connection

    /**
     * Returns a [Flow] of incoming WebTransport sessions (only for server endpoints).
     */
    fun incomingSessions(): Flow<Connection>

    /**
     * Closes the endpoint and releases all associated resources.
     */
    override fun close()
}

/**
 * Creates a client WebTransport endpoint.
 * @param certificateHashes Optional list of SHA-256 certificate hashes to trust (for self-signed certs).
 */
expect fun createClientEndpoint(
    certificateHashes: List<String> = emptyList()
): Endpoint

/**
 * Creates a server WebTransport endpoint.
 * @param bindAddr The address to bind to (e.g., "0.0.0.0:4433").
 * @param certificate The server identity (certificate and private key).
 */
expect fun createServerEndpoint(
    bindAddr: String,
    certificate: Certificate
): Endpoint
