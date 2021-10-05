package paperkuts.utils

import java.io.File
import java.io.InputStream
import java.nio.charset.Charset
import java.util.*


fun Properties(source: InputStream, charset: Charset = Charsets.UTF_8): Properties {
    return Properties().apply {
        load(source.reader(charset))
    }
}

fun Properties(file: File): Properties {
    return file.inputStream().use { Properties(it) }
}

const val INDEX_NOT_FOUND: Int = -1