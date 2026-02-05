package ovh.devcraft.kwtransport

internal object DatagramHelper {
    init {
        KwTransport
    }

    @JvmStatic
    external fun getData(handle: Long): ByteArray

    @JvmStatic
    external fun destroy(handle: Long)
}
