package io.github.arosov.kwtransport

import java.net.ServerSocket

object TestUtils {
    fun getFreePort(): Int {
        return ServerSocket(0).use { it.localPort }
    }
}
