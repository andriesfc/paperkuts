package paperkuts.collections

import assertk.all
import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

internal class ListsKtTest {

    @Test
    fun testUseOf() {
        val list = ArrayList<Int>().of(1, 2, 3, 6, 8, 7, -1)
        val expected = listOf(1, 2, 3, 6, 8, 7, -1)
        assertThat(list).all {
            isEqualTo(expected)
            transform("list") { it.list() }.isEqualTo(expected)
        }
    }
}