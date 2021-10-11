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
fun Path.file(): File = toFile()
fun File.path(): Path = toPath()
fun path(string: String): Path = Path.of(string)
fun path(path: Path, vararg more: String): Path = Path.of(path.toString(), *more)
fun path(first: String, vararg more: String): Path = Path.of(first, *more)
fun Path.length(): Int = nameCount
fun Path.resolve(vararg linkOptions: LinkOption): Path = toAbsolutePath().toRealPath(*linkOptions)
fun Path.isEmpty(): Boolean = nameCount == 0

operator fun <T> Parts<T>.component1(): T? = parent
operator fun Parts<*>.component2(): String = name
operator fun Parts<*>.component3(): String? = extension
operator fun Parts<*>.component4(): String = baseName

operator fun Path.get(index: Int): Path = getName(index)


fun Path.parts(): Parts<Path> = PathParts(this)

private const val ROOT_PATH = "/"
private const val NAME_PART_DELIMITER = '.'

private class PathParts(private val path: Path) : Parts<Path> {

    private val pathString = path.toString()
    override val parent: Path? = path.parent

    override val name: String = if (pathString == ROOT_PATH) {
        ROOT_PATH
    } else {
        path.name
    }


    override val extension: String? = when (val index = name.indexOf(NAME_PART_DELIMITER)) {
        INDEX_NOT_FOUND -> null
        else -> name.substring(index)
    }


    override val baseName: String = name.substringBefore(
        delimiter = '.',
        missingDelimiterValue = name
    )

    override fun equals(other: Any?): Boolean {
        return when {
            other == null -> false
            other === this -> true
            other !is PathParts -> false
            else -> path == other.path
        }
    }

    override fun hashCode(): Int = Objects.hash(path)
}

fun Path.join(path: String, vararg more: String): Path {

    val adding = when {
        more.isEmpty() -> path(path)
        else -> path(path, *more)
    }

    return if (isEmpty()) {
        adding
    } else {
        path(this, adding.toString())
    }

}

fun Path.remove(fromIndex: Int, howManyNames: Int): Path {

    val endIndex = (fromIndex + howManyNames); checkBoundsFor("remove", fromIndex, endIndex)

    if (howManyNames == 0) {
        return this
    }

    if (fromIndex == 0 && endIndex == length()) {
        return emptyPath
    }

    if (fromIndex == length() - 1 && endIndex == length()) {
        return emptyPath
    }

    if (fromIndex == 0) {
        return subpath(endIndex, length())
    }

    if (endIndex == length()) {
        return subpath(0, fromIndex)
    }

    val before = subpath(0, fromIndex).toString()
    val after = subpath(endIndex, length()).toString()
    return path(before, after)
}


fun Path.insert(index: Int, pathString: String, vararg more: String): Path {
    return insert(index, path(pathString), *more)
}

fun Path.insert(index: Int, path: Path, vararg more: String): Path {

    checkBoundsFor("insert", index)

    val inserting = when {
        more.isEmpty() -> "$path"
        else -> path("$path", *more).toString()
    }

    if (index == 0) {
        return Path.of(inserting, toString())
    }

    if (index == length()) {
        return Path.of(toString(), inserting)
    }

    val before = subpath(0, index).toString()
    val after = subpath(index, length()).toString()

    return Path.of(before, inserting, after)
}

private fun Path.checkBoundsFor(operation: String, start: Int, end: Int = length()) {
    if (start < 0 || end > length()) {
        throw IndexOutOfBoundsException(
            "%s (available: %d; start=%d; end=%d)".format(
                operation,
                length(),
                start,
                end
            )
        )
    }
}

private val emptyPath = path("")

/**
 * Slices off one or more pieces from an existing path and returns it.
 * @receiver Path
 * @param from Int
 * @param howManyNames Int
 * @return Path
 * @throws IndexOutOfBoundsException
 *
 *  - If the index is less than zero
 *  - If the number of parts remaining after the index is less than the requested number of parts
 */
fun Path.slice(from: Int, howManyNames: Int = length()): Path {

    val endIndex = from + howManyNames; checkBoundsFor("slice", from, endIndex)

    if (howManyNames == 0) {
        return emptyPath
    }

    return subpath(from, endIndex)
}


fun Path.withPrefix(prefix: Path): Path {

    if (startsWith(prefix)) {
        return this
    }

    return insert(0, prefix)
}

fun Path.withSuffix(suffix: Path): Path {

    if (endsWith(suffix)) {
        return this
    }

    return insert(length(), suffix)
}


