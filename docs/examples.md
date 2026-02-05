# kwtransport Usage Examples

This document provides a series of practical examples demonstrating how to use the `kwtransport` library for common WebTransport operations.

## 1. Creating a Self-Signed Certificate

For local development and testing, you can easily generate a self-signed TLS certificate.

```kotlin
import ovh.devcraft.kwtransport.Certificate
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    // Create a self-signed certificate valid for localhost
    Certificate.createSelfSigned("localhost").use { certificate ->
        println("Certificate created with SHA-256 hash: ${certificate.getHash()}")
        // The certificate will be automatically closed at the end of the `use` block
    }
}
```

## 2. Setting Up a Basic Server

This example demonstrates how to start a server endpoint, listen for incoming connections, and handle a new session.

```kotlin
import ovh.devcraft.kwtransport.Certificate
import ovh.devcraft.kwtransport.Endpoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val certificate = Certificate.createSelfSigned("localhost")

    Endpoint.createServerEndpoint("127.0.0.1:4433", certificate).use { server ->
        println("Server started. Listening on ${server.localAddr}")

        // Accept incoming connections in a separate coroutine
        val serverJob = launch {
            server.incomingSessions().collect { connection ->
                println("New connection received!")
                // Handle the connection (e.g., accept streams)
                connection.close(0, "Connection handled successfully.")
            }
        }

        // Keep the server running for a while in this example
        // In a real application, you'd manage the server's lifecycle differently.
        kotlinx.coroutines.delay(60_000) // Run for 1 minute
        serverJob.cancel()
    }
    certificate.close()
}
```

## 3. Connecting a Client

Here's how a client connects to the server. For this example, we'll trust the self-signed certificate from the server by accepting all certificates.

**Note:** In a production environment, you should use `certificateHashes` to trust specific server certificates instead of `acceptAllCerts = true`.

```kotlin
import ovh.devcraft.kwtransport.Endpoint
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    Endpoint.createClientEndpoint(acceptAllCerts = true).use { client ->
        try {
            val connection = client.connect("https://127.0.0.1:4433")
            println("Successfully connected to the server!")
            // Use the connection...
            connection.close(0, "Client finished.")
        } catch (e: Exception) {
            println("Failed to connect: ${e.message}")
        }
    }
}
```

## 4. Working with Streams

### Bidirectional Stream (Client opens, Server echoes)

This example shows a client opening a bidirectional stream, sending a message, and the server echoing it back.

**Server Code:**
```kotlin
// Inside the server's incomingSessions().collect block:
launch {
    try {
        val streamPair = connection.acceptBi()
        println("Accepted bidirectional stream from client.")

        // Read data using the Flow API
        streamPair.recv.chunks().collect { chunk ->
            val receivedMessage = chunk.toString(Charsets.UTF_8)
            println("Server received: '$receivedMessage'")
            
            // Echo the message back
            val response = "Echo: $receivedMessage"
            streamPair.send.write(response)
            println("Server sent: '$response'")
        }
    } catch (e: Exception) {
        println("Server stream error: ${e.message}")
    }
}
```

**Client Code:**
```kotlin
// Inside the client's successful connection block:
val streamPair = connection.openBi()
println("Client opened a bidirectional stream.")

// Send a message
val message = "Hello, Server!"
streamPair.send.write(message)
println("Client sent: '$message'")

// Read the response
val responseChunk = streamPair.recv.chunks().first() // Using .first() for a single response
val responseMessage = responseChunk.toString(Charsets.UTF_8)
println("Client received: '$responseMessage'")

streamPair.send.close()
streamPair.recv.close()
```

### Unidirectional Stream (Server sends a welcome message)

**Server Code:**
```kotlin
// Inside the server's incomingSessions().collect block:
launch {
    try {
        val sendStream = connection.openUni()
        println("Server opening unidirectional stream to send welcome message.")
        sendStream.write("Welcome to the server!")
        sendStream.close()
    } catch (e: Exception) {
        println("Server stream error: ${e.message}")
    }
}
```

**Client Code:**
```kotlin
// Inside the client's successful connection block:
try {
    val recvStream = connection.acceptUni()
    println("Client accepted a unidirectional stream from server.")

    val welcomeMessage = recvStream.chunks().first().toString(Charsets.UTF_8)
    println("Client received welcome message: '$welcomeMessage'")
    recvStream.close()
} catch (e: Exception) {
    println("Client stream error: ${e.message}")
}
```

## 5. Sending and Receiving Datagrams (Unreliable)

Datagrams are useful for sending data where delivery is not guaranteed (e.g., real-time game state).

**Server Code:**
```kotlin
// Inside the server's incomingSessions().collect block:
launch {
    try {
        while (true) {
            val datagram = connection.receiveDatagram()
            println("Server received datagram: '${datagram.toString(Charsets.UTF_8)}'")
        }
    } catch (e: Exception) {
        // This will likely happen when the connection closes
    }
}
```

**Client Code:**
```kotlin
// Inside the client's successful connection block:
println("Client sending a datagram.")
connection.sendDatagram("This is an unreliable datagram!".toByteArray())
// There is no guarantee the server will receive this.
```
