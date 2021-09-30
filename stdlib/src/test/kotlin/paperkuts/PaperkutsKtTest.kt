package paperkuts

import assertk.all
import assertk.assertAll
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isSuccess
import org.junit.jupiter.api.Test

internal class PaperkutsKtTest {

    @Test
    fun pair() {
        val p = paperkuts.pair(6, 'A')
        assertThat(p).all {
            val (a, b) = p
            assertThat(a).isEqualTo(6)
            assertThat(b).isEqualTo('A')
        }
    }

    @Test
    fun trace() {
        val ex = Exception("1").also { x1 ->
            x1.initCause(Exception("2").also { x2 ->
                x2.initCause(Exception("3").also { x3 ->
                    x3.initCause(Exception("4"))
                })
            })
        }
        val trace = ex.trace().mapNotNull(Throwable::message).toList()
        assertThat(trace).isEqualTo(listOf("1", "2", "3", "4"))
    }

    @Test
    fun rootCause() {
        val ex = Exception("1").also { x1 ->
            x1.initCause(Exception("2").also { x2 ->
                x2.initCause(Exception("3").also { x3 ->
                    x3.initCause(Exception("4"))
                })
            })
        }
        val rootCause = ex.rootCause()?.message
        assertThat(rootCause).isNotNull().isEqualTo("4")
    }

    @Test
    fun unformatted() {
        val formatted = """
            This is line one.
                This is line two.
                    This is line tree.
            This is the end.
        """.trimIndent()
        val expected = "This is line one. This is line two. This is line tree. This is the end."
        assertThat { formatted.unformatted() }.isSuccess().isEqualTo(expected)
    }

    @Test
    fun prefixedChar() {
        assertAll {
            assertThat("name".prefixed('.')).isEqualTo(".name")
            assertThat(".name".prefixed('.')).isEqualTo(".name")
        }
    }

    @Test
    fun prefixedString() {
        assertAll {
            assertThat("kitty".prefixed("hello-")).isEqualTo("hello-kitty")
            assertThat("hello-kitty".prefixed("hello-")).isEqualTo("hello-kitty")
        }
    }

    @Test
    fun array() {
        assertThat { array('1', '2', "3") }
            .isSuccess()
            .isEqualTo(arrayOf('1', '2', "3"))
    }
}