package paperkuts

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class NamedValueReplacerTest {

    private lateinit var replacer: NamedValueReplacer

    @BeforeEach
    fun beforeEach() {
        replacer = NamedValueReplacer(
            keyPrefix = "{{",
            keySuffix = "}}",
            prepareKey = String::trim
        )
    }

    @Test
    fun test() {
        val source = "{{ greeting }} {{ person }}"
        val replacements = mapOf("greeting" to "hello", "person" to "world")
        val expected = "hello world"
        val actual = replacer.replacedNamed(source, replacements::containsKey, replacements::get)
        assertThat(actual).isEqualTo(expected)
    }

}