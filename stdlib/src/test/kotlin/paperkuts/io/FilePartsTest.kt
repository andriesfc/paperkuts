package paperkuts.io

import assertk.assertThat
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import paperkuts.testing.arguments
import java.io.File


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class FilePartsTest {

    @ParameterizedTest
    @MethodSource("pathExpectations")
    fun testParts(file: File, expectation: PartExpectation) {
        assertThat(file.parts()).isAsExpected(expectation)
    }

    private fun pathExpectations() = mapOf(
        File("") to PartExpectation(
            parent = null,
            name = "",
            extension = null,
            baseName = ""
        ),
        File("todo.md") to PartExpectation(
            parent = null,
            name = "todo.md",
            extension = ".md",
            baseName = "todo"
        ),
        File("data/backups/yellow1.zip") to PartExpectation(
            parent = "data/backups",
            name = "yellow1.zip",
            baseName = "yellow1",
            extension = ".zip"
        ),
        File("./data/backups/yellow1.zip") to PartExpectation(
            parent = "./data/backups",
            name = "yellow1.zip",
            baseName = "yellow1",
            extension = ".zip"
        )
    ).arguments()

}