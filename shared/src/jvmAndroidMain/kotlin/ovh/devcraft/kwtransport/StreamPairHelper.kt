package ovh.devcraft.kwtransport

internal object StreamPairHelper {
    init {
        KwTransport
    }

    @JvmStatic
    external fun getSend(handle: Long): Long

    @JvmStatic
    external fun getRecv(handle: Long): Long

    @JvmStatic
    external fun destroy(handle: Long)
}
