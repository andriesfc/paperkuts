package paperkuts.nio.file

import assertk.Assert
import assertk.assertThat
import assertk.assertions.containsAll
import assertk.assertions.isEqualTo
import assertk.assertions.prop
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import paperkuts.assertk.peek
import paperkuts.io.PartExpectation
import paperkuts.io.Parts
import paperkuts.io.isAsExpected
import paperkuts.io.isDestructedTo
import java.nio.file.Path

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class PathKtTest {

    @ParameterizedTest
    @MethodSource("testPartsArguments")
    fun testParts(pathString: String, expectation: PartExpectation) {
        assertThat(path(pathString))
            .parts().peek { printoutActualParts(pathString, it) }
            .isAsExpected(expectation)
    }

    @ParameterizedTest
    @MethodSource("testDestructuringArguments")
    fun testDestructuring(pathString: String, expected: Array<out String>) {
        assertThat(path(pathString)).parts().isDestructedTo(expected)
    }

    @Test
    fun testJoiningOfPaths() {
        val parent = path("~/.local")
        val config = parent.join("data/d1.config").toString()
        assertThat(config).isEqualTo("~/.local/data/d1.config")
    }

    private fun printoutActualParts(pathString: String, actual: Parts<Path>) {
        println("- [ACTUAL] \"%s\": {".format(pathString))
        println("\t   parent: [%s]".format(actual.parent))
        println("\t     name: [%s]".format(actual.name))
        println("\t baseName: [%s]".format(actual.baseName))
        println("\textension: [%s]".format(actual.extension))
        println("}")
    }

    private fun testDestructuringArguments(): List<Array<Any>> {
        return testPartsArguments().map { args ->
            val path = args[0] as String
            val expected = args[1] as PartExpectation
            arrayOf(
                path,
                arrayOf(expected.parent, expected.name, expected.extension, expected.baseName)
            )
        }
    }


    @Language("json")
    private fun testPartsArguments(): List<Array<Any>> =
        Json.decodeFromString<Map<String, PartExpectation>>(
            """
            {
                "/": {
                  "parent": null,
                  "name": "/",
                  "extension": null,
                  "baseName": "/"
                },
                "/home/users/janus": {
                  "parent": "/home/users",
                  "name": "janus",
                  "extension": null,
                  "baseName": "janus"
                },
                "my-notes.txt": {
                  "parent": null,
                  "name": "my-notes.txt",
                  "baseName": "my-notes",
                  "extension": ".txt"
                },
                "/home/users/janus/documents/my-notes.txt": {
                  "parent": "/home/users/janus/documents",
                  "name": "my-notes.txt",
                  "baseName": "my-notes",
                  "extension": ".txt"
                },
                "../backups/1/catalog.d": {
                  "parent": "../backups/1",
                  "name": "catalog.d",
                  "baseName": "catalog",
                  "extension": ".d"
                }
            }
        """.trimIndent()
        ).map { arrayOf(it.key, it.value) }


    private fun Assert<Path>.parts(): Assert<Parts<Path>> {
        return transform("parts", Path::parts)
    }


}
