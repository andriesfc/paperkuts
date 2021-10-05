package paperkuts.nio.file

import paperkuts.io.Parts
import paperkuts.utils.INDEX_NOT_FOUND
import java.io.File
import java.net.URI
import java.net.URL
import java.nio.file.LinkOption
import java.nio.file.Path
import java.util.*
import kotlin.io.path.name

fun URI.path(): Path = Path.of(this)
fun URL.path(): Path = Path.of(toURI())
fun File.path(): Path = toPath()
fun path(string: String): Path = Path.of(string)
fun path(first: String, vararg more: String): Path = Path.of(first, *more)
fun Path.size(): Int = nameCount
fun Path.realpath(vararg linkOptions: LinkOption): Path = toAbsolutePath().toRealPath(*linkOptions)


operator fun <T> Parts<T>.component1(): T? = parent
operator fun Parts<*>.component2(): String = name
operator fun Parts<*>.component3(): String? = extension
operator fun Parts<*>.component4(): String = baseName


fun Path.parts(): Parts<Path> = PartsOfPath(this)

private const val ROOT_PATH = "/"

private class PartsOfPath(private val path: Path) : Parts<Path> {

    private val pathString = path.toString()

    override val parent: Path? by lazy(LazyThreadSafetyMode.NONE) {
        path.parent
    }

    override val name: String by lazy(LazyThreadSafetyMode.NONE) {
        if (pathString == ROOT_PATH) {
            ROOT_PATH
        } else {
            path.name
        }
    }

    override val extension: String? by lazy(LazyThreadSafetyMode.NONE) {
        when (val index = name.indexOf('.')) {
            INDEX_NOT_FOUND -> null
            else -> name.substring(index)
        }
    }

    override val baseName: String by lazy(LazyThreadSafetyMode.NONE) {
        name.substringBefore(
            delimiter = '.',
            missingDelimiterValue = name
        )
    }

    override fun equals(other: Any?): Boolean {
        return when {
            other == null -> false
            other === this -> true
            other !is PartsOfPath -> false
            else -> path == other.path
        }
    }

    override fun hashCode(): Int = Objects.hash(path)
}

fun Path.join(subPath: String, vararg more: String): Path {
    return Path.of(toString(), subPath).let { root ->
        when {
            more.isEmpty() -> root
            else -> Path.of(root.toString(), *more)
        }
    }
}


