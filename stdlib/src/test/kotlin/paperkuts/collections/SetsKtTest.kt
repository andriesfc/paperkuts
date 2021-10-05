package paperkuts.collections

import assertk.all
import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test
import java.util.*

internal class SetsKtTest {

    @Test
    fun testUseOf() {
        val set = TreeSet<Int>().of(6, 1, 8)
        val expected = sortedSetOf(6, 1, 8)
        val readonlySet = expected.toSet()
        assertThat(set).all {
            isEqualTo(expected)
            transform("set") { it.set() }.isEqualTo(readonlySet)
        }
    }

}