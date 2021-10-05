package paperkuts.io

import paperkuts.utils.INDEX_NOT_FOUND
import java.io.File
import java.io.IOException
import java.util.*

enum class OnNeeding {
    CreateIfNotExists,
    FailEarly
}

fun File.needsParent(needing: OnNeeding = OnNeeding.CreateIfNotExists): File {
    return apply {

        if (parentFile.exists()) {
            return@apply
        }

        when (needing) {
            OnNeeding.FailEarly -> {
                throw IOException("parent directory required for $name: $parent")
            }
            OnNeeding.CreateIfNotExists -> if (!mkdirs()) {
                throw IOException("unable to create parent for $name: $parent")
            }
        }

    }
}

fun File.parts(): Parts<File> = FileParts(this)


private class FileParts(private val file: File) : Parts<File> {

    override val parent: File? by lazy(LazyThreadSafetyMode.NONE) { file.parentFile }
    override val name: String by lazy(LazyThreadSafetyMode.NONE) { file.name }

    override val extension: String? by lazy(LazyThreadSafetyMode.NONE) {
        when (val index = name.indexOf('.')) {
            INDEX_NOT_FOUND -> null
            else -> name.substring(index)
        }
    }

    override val baseName: String by lazy {
        when (val index = name.indexOf('.')) {
            INDEX_NOT_FOUND -> name
            else -> name.substring(0, index)
        }
    }

    override fun hashCode(): Int {
        return Objects.hash(file)
    }

    override fun equals(other: Any?): Boolean {
        return when {
            other === this -> true
            other is FileParts -> other.file == file
            else -> false
        }
    }
}
