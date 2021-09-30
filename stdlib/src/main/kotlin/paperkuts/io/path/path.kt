package paperkuts.io.path

import java.io.File
import java.net.URI
import java.net.URL
import java.nio.file.Path

fun URI.path(): Path = Path.of(this)
fun URL.path(): Path = Path.of(toURI())
fun File.path(): Path = toPath()
fun path(string: String) = Path.of(string)
fun psth(first: String, vararg more: String): Path = Path.of(first, *more)