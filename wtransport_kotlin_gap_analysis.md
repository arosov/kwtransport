# wtransport vs. kwtransport: Gap Analysis

This document outlines the features available in the underlying `wtransport` Rust crate that are currently **missing** or **not exposed** in the `kwtransport` Kotlin wrapper.

## 1. Client Configuration (`ClientConfig`)

The current Kotlin `Endpoint.createClientEndpoint` exposes:
- `bindAddr`: Bind address.
- `acceptAllCerts`: Boolean toggle (Native Certs vs. No Validation).
- `maxIdleTimeoutMillis`: Idle timeout.
- **Certificate Pinning (`certificateHashes`)**: Supported.
- **Keep-Alive Interval (`keepAliveIntervalMillis`)**: Supported.
- **IPv6 Dual Stack (`ipv6DualStackConfig`)**: Supported.
- **Custom Transport Config (`quicConfig`)**: Supported (stream limits, flow control, buffer sizes).
- **Advanced TLS Config (`with_custom_tls`)**: Supported (Client Authentication, Custom Root CAs).

### Missing Features:
- **Custom DNS Resolver (`dns_resolver`)**: Ability to plug in a custom DNS resolver (currently hardcoded to `TokioDnsResolver`). (Low Priority / High Complexity)
- **Bind to Pre-existing Socket (`with_bind_socket`)**: Ability to pass an existing `UdpSocket`.

## 2. Server Configuration (`ServerConfig`)

The current Kotlin `Endpoint.createServerEndpoint` exposes:
- `bindAddr`: Bind address.
- `certificate`: Identity (cert + key).
- **Max Idle Timeout (`maxIdleTimeoutMillis`)**: Supported.
- **Keep-Alive Interval (`keepAliveIntervalMillis`)**: Supported.
- **Migration Support (`allowMigration`)**: Supported.
- **IPv6 Dual Stack (`ipv6DualStackConfig`)**: Supported.
- **Custom Transport Config (`quicConfig`)**: Supported.

### Missing Features:
- **Bind to Pre-existing Socket (`with_bind_socket`)**: Ability to pass an existing `UdpSocket`.
- **Advanced TLS Config (`with_custom_tls`)**: Custom `rustls::ServerConfig`. (Low Priority as Server Identity is already configurable via `Certificate`)

## 3. Events & Observability

- **Connection Stats**: Supported (`rttMs`, `lostPackets`, `sentPackets`, `congestionEvents`).
- **Datagram Events**: The current API has `sendDatagram` / `receiveDatagram`, but `wtransport` (via Quinn) exposes more granular events or stats which might be useful. (Medium Priority)
- **Congestion Control**: No exposure of congestion controller statistics or configuration. (Low Priority)

## 4. Error Handling

- **Error Granularity**: Supported. `ConnectionException` now includes `errorCode` and `reason` for application-closed connections.
- **Disconnection Reasons**: Supported.

## 5. Streams

- **Stream Priority**: Supported. `SendStream.setPriority` / `SendStream.getPriority`.
- **Unidirectional Streams Management**: While supported, the API for managing the *number* of concurrent streams (flow control) is implicit via `acceptUni`/`openUni` but explicit limits (MaxConcurrentStreams) are not configurable.

## Recommendation

High priority gaps have been addressed. Future work could focus on advanced features like **Stream Priority** or **Custom DNS Resolver** if specific needs arise.
