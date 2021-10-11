package paperkuts.junit.jupiter.support

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isSuccess
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class SupportKtTest {

    @ParameterizedTest
    @MethodSource("testSentencesFromNameExpectations")
    fun testSentenceFromName(expectation: Expectation<String, String>) {
        assertThat { nameToSentence(expectation.given) }.isSuccess().isEqualTo(expectation.wanted)
    }

    private fun testSentencesFromNameExpectations(): List<Expectation<String, String>> {
        return listOf(
            Expectation(
                description = "Name with dots and underscores should translate to a sentence",
                given = "name.with.dotsAndUnderscore.shouldTranslateToASentence",
                wanted = "Name with dots and underscore should translate to a sentence"),
            Expectation(
                description = "Camel case name starting with lower case letter should become a sentence",
                given = "camelCaseStartingWithLowercaseLetter",
                wanted = "Camel case starting with lowercase letter"
            ),
            Expectation(
                description = "Name with underscores should be treated as white space in the sentence",
                given = "name_with_underscores__should_be_a_sentence",
                wanted = "Name with underscores should be a sentence"
            ),
            Expectation(
                description = "Names with dashes",
                given = "special-name-with---dashes",
                wanted = "Special name with dashes"
            )
        )
    }

}