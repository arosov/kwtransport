package ovh.devcraft.kwtransport

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EndpointTest {
    @Test
    fun testCreateAndDestroy() {
        val endpoint = Endpoint.createClientEndpoint()
        assertFalse(endpoint.isClosed())
        endpoint.close()
        assertTrue(endpoint.isClosed())
    }
}
