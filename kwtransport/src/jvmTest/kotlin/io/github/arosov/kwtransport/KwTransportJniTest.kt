package io.github.arosov.kwtransport

import kotlin.test.Test
import kotlin.test.assertEquals

class KwTransportJniTest {
    @Test
    fun testHello() {
        assertEquals("Hello from Rust!", KwTransport.hello())
    }
}