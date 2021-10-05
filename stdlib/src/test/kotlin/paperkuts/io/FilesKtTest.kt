package paperkuts.io

import assertk.assertThat
import assertk.assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import paperkuts.assertk.peek
import paperkuts.io.DoWhenFileNotExists.CreateIfNotExists
import paperkuts.io.DoWhenFileNotExists.FailEarly
import java.io.File
import java.io.IOException

internal class FilesKtTest {

    @Test
    fun testParentRequired(@TempDir dir: File) {
        assertThat {
            File(
                dir,
                "/notes/note1.txt"
            ).needsParent(CreateIfNotExists)
        }.isSuccess().prop(File::getParentFile).exists()
    }

    @Test
    fun testParentRequiredFailIfNotCreating(@TempDir dir: File) {
        assertThat {
            File(
                dir,
                "/notes/note2.txt"
            ).needsParent(FailEarly)
        }.isFailure().isInstanceOf(IOException::class).peek { println(it.message) }
    }

}