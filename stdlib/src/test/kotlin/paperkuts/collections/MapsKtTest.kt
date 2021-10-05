package paperkuts.collections

import assertk.all
import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

internal class MapsKtTest {

    @Test
    fun testUseOf() {
        val m = LinkedHashMap<Int, String>().of(1 to "one")
        val expected = mapOf(1 to "one")
        assertThat(m).all {
            isEqualTo(expected)
            transform("map") { it.mapped() }.isEqualTo(expected)
        }
        assertThat(m).isEqualTo(expected)
    }
}