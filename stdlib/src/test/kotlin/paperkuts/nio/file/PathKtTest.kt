package paperkuts.nio.file

import assertk.Assert
import assertk.assertThat
import assertk.assertions.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.MethodSource
import paperkuts.assertk.peek
import paperkuts.io.PartExpectation
import paperkuts.io.Parts
import paperkuts.io.isAsExpected
import paperkuts.io.isDestructedTo
import java.nio.file.Path

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class PathKtTest {

    @Nested
    inner class SliceTests {

        private val path = path("this/is/a/path/to-a/file.txt")
        private val pathLength = 6

        @Test
        fun sliceWithZeroPartsShouldReturnEmptyPath() {
            assertThat(path.slice(0, 0)).isEqualTo(path(""))
        }

        @Test
        fun sliceWithOnePartShouldReturnSingleName() {
            assertThat(path.slice(0, 1)).isEqualTo(path("this"))
        }

        @Test
        fun sliceBetweenShouldReturnExpectedSubPath() {
            assertThat(path.slice(1, 3)).isEqualTo(path("is/a/path"))
        }

        @Test
        fun sliceBeforeZeroShouldFailWithOutOfBoundsException() {
            assertThat { path.slice(-1) }
                .isFailure()
                .isInstanceOf(IndexOutOfBoundsException::class)
        }

        @Test
        fun sliceAfterSizeShouldFailWithOutOfBoundsException() {
            assertThat { path.slice(pathLength + 1) }
                .isFailure()
                .isInstanceOf(IndexOutOfBoundsException::class)
        }

    }

    @Nested
    inner class TestInsertIntoPath {

        private val path = path("this/is/a/path/to-a/place")
        private val pathToInsert = path("new-path/to-a/another-place")

        @Test
        fun insertingBeforeBoundsShouldFailWithIndexOutOfBounds() {
            assertThat { path.insert(-1, path("hello")) }
                .isFailure()
                .isInstanceOf(IndexOutOfBoundsException::class)
        }

        @Test
        fun totalPathLengthShouldBeEqualToOldLengthPlusInserted() {
            val expectedPathLength = pathToInsert.length() + path.length()
            assertThat { path.insert(0, pathToInsert) }
                .isSuccess()
                .transform("length") { it.length() }
                .isEqualTo(expectedPathLength)
        }

        @Test
        fun insertAtEndOfPath() {
            val expected = path("this/is/a/path/to-a/place/new-path/to-a/another-place")
            assertThat(path.insert(path.length(), pathToInsert)).isEqualTo(expected)
        }

        @Test
        fun insertAtStartOfPath() {
            val expected = path("new-path/to-a/another-place/this/is/a/path/to-a/place")
            assertThat(path.insert(0, pathToInsert)).isEqualTo(expected)
        }

        @Test
        fun insertInBetweenOfPath() {
            val initial = path("0/1/2/3/4/5")
            val inserting = path("a/b/c")
            val insertIndex = 2
            val expectedAfterInsert = path("0/1/a/b/c/2/3/4/5")
            assertThat(initial.insert(insertIndex, inserting)).isEqualTo(expectedAfterInsert)
        }
    }

    @Nested
    inner class TestRemoveFromPath {

        private val path = path("1/2/3/4/5/6")

        @Test
        fun removingBeforeStartShouldNotBePossible() {
            assertThat { path.remove(-1, 1) }
                .isFailure()
                .isInstanceOf(IndexOutOfBoundsException::class)
        }

        @Test
        fun removingAfterEndShouldNotBePossible() {
            assertThat { path.remove(10, 1) }
                .isFailure()
                .isInstanceOf(IndexOutOfBoundsException::class)
        }

        @Test
        fun removingTheEntireLengthShouldReturnEmptyPath() {
            val emptyPath = path("")
            assertThat(path.remove(0, path.length())).isEqualTo(emptyPath)
        }

        @Test
        fun removeFromStartOfPath() {
            val partsToRemove = 2
            val expected = path("3/4/5/6")
            assertThat(path.remove(0, partsToRemove)).isEqualTo(expected)
        }

        @Test
        fun removeEndOfPath() {
            val partsToRemove = 2
            val actual = path.remove(4, partsToRemove)
            val expected = path("1/2/3/4")
            assertThat(actual).isEqualTo(expected)
        }

        @Test
        fun removeInBetweenOfPath() {
            val partsToRemove = 2
            val actual = path.remove(2, partsToRemove)
            val expected = path("1/2/5/6")
            assertThat(actual).isEqualTo(expected)
        }
    }

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

    @ParameterizedTest
    @CsvSource(
        "backups/urgent,    backups,    backups/urgent",
        "urgent,            backups,    backups/urgent"
    )
    fun pathWithPrefix(path: String, prefix: String, expectedPrefixPath: String) {
        assertThat(path(path).withPrefix(path(prefix))).isEqualTo(path(expectedPrefixPath))
    }


    @Nested
    inner class TestPathWithSuffix {




    }

}

