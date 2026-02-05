package ovh.devcraft.kwtransport

import java.net.InetAddress

interface CustomDnsResolver {
    /**
     * Resolves a hostname to a list of IP addresses.
     * If resolution fails or no addresses are found, an empty list or null can be returned.
     */
    suspend fun resolve(host: String): List<InetAddress>
}
