package ovh.devcraft.kwtransport

import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import kotlin.test.*
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

class FfiStressTest {

    @Test
    fun `sanity check create and close`() {
        val client = Endpoint.createClientEndpoint()
        client.close()
    }

    @Test
    fun `use after close should fail gracefully`() = runTest {
        val client = Endpoint.createClientEndpoint()
        client.close()

        assertFailsWith<IllegalStateException> {
            client.connect("https://127.0.0.1:4433")
        }
    }

    @Test
    fun `massive create and close loop for memory leaks`() {
        // This test mainly checks that we don't crash or OOM quickly.
        // Real leak detection requires external monitoring or heap dumps.
        val iterations = 10_000
        repeat(iterations) {
            val client = Endpoint.createClientEndpoint()
            client.close()
        }
    }

    @Test
    fun `concurrent double close stress test`() = runTest {
        // This attempts to race two threads calling close() on the same object.
        // If the implementation is not thread-safe, this might crash the JVM.
        
        val iterations = 1000
        val crashes = AtomicInteger(0)
        
        repeat(iterations) {
            val client = Endpoint.createClientEndpoint()
            
            val job1 = launch(Dispatchers.Default) {
                client.close()
            }
            val job2 = launch(Dispatchers.IO) {
                client.close()
            }
            
            joinAll(job1, job2)
        }
    }

    @Test
    fun `thread affinity violation check`() = runTest {
        // Create on one thread, use on another, close on third.
        // Rust's wtransport Endpoint is Send+Sync so this should be fine,
        // but we verify no thread-local assumptions are violated.
        
        val dispatcherA = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
        val dispatcherB = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
        val dispatcherC = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
        
        val client = withContext(dispatcherA) {
            Endpoint.createClientEndpoint()
        }
        
        // Just checking handle validity effectively
        withContext(dispatcherB) {
            assertFalse(client.isClosed())
        }
        
        withContext(dispatcherC) {
            client.close()
        }
        
        assertTrue(client.isClosed())
        
        dispatcherA.close()
        dispatcherB.close()
        dispatcherC.close()
    }
}
