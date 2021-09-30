package paperkuts.collections

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

internal class ListsKtTest {

    @Test
    fun buildList() {
        val expected = listOf(1, 2, 3, -3, -2, -1)
        val actual: List<Int> = buildList { add(1); add(2, 3); add(-3, -2, -1) }
        assertThat(actual).isEqualTo(expected)
    }

}