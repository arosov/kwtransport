package io.github.arosov.kwtransport

import org.khronos.webgl.Uint8Array
import org.khronos.webgl.Int8Array
import org.khronos.webgl.ArrayBuffer

internal fun Uint8Array.asByteArray(): ByteArray {
    return Int8Array(buffer, byteOffset, length).asDynamic() as ByteArray
}

internal fun ByteArray.toUint8Array(): Uint8Array {
    return Uint8Array(this.asDynamic().buffer as ArrayBuffer, this.asDynamic().byteOffset as Int, this.size)
}
