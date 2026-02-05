import kotlinx.coroutines.*
import ovh.devcraft.kwtransport.Certificate
import ovh.devcraft.kwtransport.Connection
import ovh.devcraft.kwtransport.Endpoint
import ovh.devcraft.kwtransport.StreamPair
import kotlinx.coroutines.flow.collect
import java.io.IOException

const val PORT = 8765
const val HOST = "127.0.0.1"

fun main(args: Array<String>) {
    if (args.contains("-s")) {
        runServer()
    } else {
        runClient()
    }
}

fun runServer() = runBlocking {
    println("Starting server on port $PORT...")
    val certificate = Certificate.createSelfSigned(HOST)
    val server = Endpoint.createServerEndpoint("$HOST:$PORT", certificate)
    println("Server listening on ${server.localAddr}")

    try {
        server.incomingSessions().collect { connection ->
            println("Client connected! Starting chat session.")
            handleConnection(connection, isServer = true)
            println("Chat session ended for this client.")
        }
    } finally {
        server.close()
        certificate.close()
        println("Server shut down.")
    }
}

fun runClient() = runBlocking {
    println("Connecting to server at $HOST:$PORT...")
    val client = Endpoint.createClientEndpoint(acceptAllCerts = true)

    try {
        val connection = client.connect("https://$HOST:$PORT")
        println("Connected to server! You can start chatting.")
        handleConnection(connection, isServer = false)
    } catch (e: Exception) {
        println("Failed to connect to server: ${e.message}")
    } finally {
        client.close()
        println("Disconnected.")
    }
}

private fun CoroutineScope.handleConnection(connection: Connection, isServer: Boolean) = runBlocking {
    val stream: StreamPair = try {
        if (isServer) {
            println("Waiting for client to open a stream...")
            connection.acceptBi()
        } else {
            println("Opening a bidirectional stream...")
            connection.openBi()
        }
    } catch (e: Exception) {
        println("Failed to establish a bidirectional stream: ${e.message}")
        return@runBlocking
    }
    println("Stream established.")

    val consoleReader = launch(Dispatchers.IO) {
        print("You: ")
        while (isActive) {
            val line = readLine()
            if (line == null || line.equals("exit", ignoreCase = true)) {
                if (isActive) cancel()
                break
            }
            try {
                stream.send.write(line)
            } catch (e: IOException) {
                println("Connection lost while sending.")
                if (isActive) cancel()
            }
        }
    }

    val networkReader = launch {
        try {
            stream.recv.chunks().collect { chunk ->
                // Use print flush to ensure prompt displays correctly
                print("\rPeer: ${chunk.toString(Charsets.UTF_8)}\nYou: ")
                System.out.flush()
            }
        } catch (e: Exception) {
            // Stream or connection closed
        } finally {
            println("\rPeer disconnected.")
        }
    }

    try {
        joinAll(consoleReader, networkReader)
    } finally {
        println("Closing connection.")
        stream.send.close()
        stream.recv.close()
        connection.close()
    }
}
