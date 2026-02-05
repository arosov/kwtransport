package ovh.devcraft.kwtransport

import kotlinx.coroutines.*
import ovh.devcraft.kwtransport.TestCertificate

fun main() = runBlocking {
    val cert = createCertificateFromPem(TestCertificate.certificatePem, TestCertificate.privateKeyPem)
    val server = createServerEndpoint("127.0.0.1:4433", cert)
    
    println("SERVER_VERSION: 3")
    println("SERVER_READY")
    println("Test Echo Server started on 127.0.0.1:4433")
    println("Fingerprint: ${TestCertificate.fingerprint}")

    try {
        server.incomingSessions().collect { connection ->
            println("Accepted session from $connection")
            launch {
                try {
                    while (true) {
                        val pair = connection.acceptBi()
                        println("Accepted bidirectional stream")
                        launch {
                            try {
                                val buffer = ByteArray(1024)
                                while (true) {
                                    val n = pair.recv.read(buffer)
                                    if (n < 0) break
                                    if (n > 0) {
                                        val data = buffer.copyOf(n)
                                        println("Echoing $n bytes")
                                        pair.send.write(data)
                                    }
                                }
                            } catch (e: Exception) {
                                println("Stream error: ${e.message}")
                            } finally {
                                pair.send.close()
                                pair.recv.close()
                            }
                        }
                    }
                } catch (e: Exception) {
                    println("Session error: ${e.message}")
                } finally {
                    connection.close()
                }
            }
        }
    } finally {
        server.close()
    }
}