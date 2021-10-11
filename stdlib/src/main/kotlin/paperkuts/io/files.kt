@file:Suppress("ClassName")

package paperkuts.io

import paperkuts.utils.INDEX_NOT_FOUND
import java.io.File
import java.io.IOException
import java.util.*

enum class ParentMissingAction {
    CREATE_PARENTS, IS_A_FAILURE
}

fun File.withParentPath(fileNotExists: ParentMissingAction): File {

    if (parentFile.exists()) {
        return this
    }

    when (fileNotExists) {
        ParentMissingAction.IS_A_FAILURE -> {
            throw IOException("parent directory required for $name: $parent")
        }
        ParentMissingAction.CREATE_PARENTS -> if (!mkdirs()) {
            throw IOException("unable to create parent for $name: $parent")
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

fun File.touch(parentMissingAction: ParentMissingAction = ParentMissingAction.CREATE_PARENTS): File {

    withParentPath(parentMissingAction)

    if (!exists() && !createNewFile()) {
        throw IOException(
            "unable to create file \"%s\" here: \"%s\"".format(
                name,
                parent
            )
        )
    }

    setLastModified(System.currentTimeMillis())

    return this
}
