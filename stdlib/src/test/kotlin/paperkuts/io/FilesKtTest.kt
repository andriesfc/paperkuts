package paperkuts.io

import assertk.assertThat
import assertk.assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import paperkuts.assertk.peek
import paperkuts.nio.file.file
import paperkuts.nio.file.path
import java.io.File
import java.io.IOException
import java.nio.file.Path

internal class FilesKtTest {

    @Test
    fun testParentRequired(@TempDir dir: File) {
        assertThat {
            File(
                dir,
                "/notes/note1.txt"
            ).withParentPath(ParentMissingAction.CREATE_PARENTS)
        }.isSuccess().prop(File::getParentFile).exists()
    }

    @Test
    fun testParentRequiredFailIfNotCreating(@TempDir dir: File) {
        assertThat {
            File(
                dir,
                "/notes/note2.txt"
            ).withParentPath(ParentMissingAction.IS_A_FAILURE)
        }.isFailure().isInstanceOf(IOException::class).peek { println(it.message) }
    }

    @Test
    fun touchNonExistingFile(@TempDir tempDir: Path) {
        val testStart = System.currentTimeMillis()
        val file = path(tempDir, "/path/to/my/dotty.txt").file()
        file.touch()
        assertThat(file).exists()
        assertThat(file).transform { it.lastModified() }.isGreaterThan(testStart)
    }

}