package paperkuts.io

import paperkuts.io.DoWhenFileNotExists.Fail
import paperkuts.io.DoWhenFileNotExists.MakeDirs
import paperkuts.nio.file.path
import paperkuts.utils.INDEX_NOT_FOUND
import java.io.File
import java.io.IOException
import java.nio.file.attribute.FileTime
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

enum class DoWhenFileNotExists {
    MakeDirs,
    Fail
}

fun File.needsParent(fileNotExists: DoWhenFileNotExists = MakeDirs): File {
    if (!parentFile.exists()) {
        when (fileNotExists) {
            Fail -> {
                throw IOException("parent directory required for $name: $parent")
            }
            MakeDirs -> if (!mkdirs()) {
                throw IOException("unable to create parent for $name: $parent")
            }
        }
    }
    return this
}

fun File.parts(): Parts<File> = FileParts(this)

private class FileParts(private val file: File) : Parts<File> {

    override val parent: File? = file.parentFile
    override val name: String = file.name

    override val extension: String? = when (val index = name.indexOf('.')) {
        INDEX_NOT_FOUND -> null
        else -> name.substring(index)
    }

    override val baseName: String = when (val index = name.indexOf('.')) {
        INDEX_NOT_FOUND -> name
        else -> name.substring(0, index)
    }

    override fun hashCode(): Int = Objects.hash(file)

    override fun equals(other: Any?): Boolean {
        return when {
            other === this -> true
            other is FileParts -> other.file == file
            else -> false
        }
    }
}

fun File.modifiedAt(time: LocalDateTime, zoneOffset: ZoneOffset) {

}

fun File.touch(): File {

    if (!exists()) {
        needsParent()
        if (!createNewFile()) {
            throw IOException(
                "Unable to create new file [%s] here: %s".format(
                    name,
                    parent
                )
            )
        }
    } else {

    }

    return this
}
