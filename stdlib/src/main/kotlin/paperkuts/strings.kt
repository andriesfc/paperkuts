package paperkuts

import paperkuts.collections.mapped
import paperkuts.utils.INDEX_NOT_FOUND
import paperkuts.utils.array
import java.nio.charset.Charset

fun String.bytes(cs: Charset = Charsets.UTF_8): ByteArray = toByteArray(cs)

fun printf(string: String, vararg args: Any?) {
    when {
        args.isEmpty() -> print(string)
        else -> print(string.format(*args))
    }
}

fun printfLine(string: String, vararg args: Any?) {
    when {
        args.isEmpty() -> println(string)
        else -> println(string.format(args))
    }
}

/**
 * A `NamedValueReplacer` replaces named keys with values supplied by function.
 *
 * The class itself is thread safe, so it ok to configure one instance
 * and re-use it.
 *
 * @property keyPrefix String The prefix for keys donating values to
 *     replace.
 * @property keySuffix String The suffix for jets donating values to
 *     replace.
 * @property prepareKey A function which takes any key found enclosed by
 *     a [keyPrefix] and [keySuffix] and returns a new sanitized value.
 * @constructor Constructs a configured thread safe instance which is
 *     re-usable
 */
class NamedValueReplacer(
    private val keyPrefix: String,
    private val keySuffix: String,
    private val prepareKey: (possibleKey: String) -> String = { it }
) {

    fun replacedNamed(
        source: String,
        replaceableKey: (String) -> Boolean,
        replaced: (String) -> Any?
    ): String {

        if (source.isEmpty()) {
            return source
        }

        val dest = StringBuilder()
        var i = 0

        while (i < source.length) {

            val a = source.indexOf(keyPrefix, i).takeUnless { it == INDEX_NOT_FOUND }
                ?: break

            val b =
                source.indexOf(keySuffix, a + keyPrefix.length).takeUnless { it == INDEX_NOT_FOUND }
                    ?: break

            val key = prepareKey(source.substring(a + keyPrefix.length, b))

            if (replaceableKey(key)) {
                dest.append(source, i, a)
                dest.append(replaced(key))
            } else {
                dest.append(source, i, b + keySuffix.length)
            }
            i = b + keySuffix.length
        }

        if (i < source.length) {
            dest.append(source, i, source.length)
        }

        return dest.toString()
    }

    companion object {
    }
}


private val defaultNamedValueReplacer = NamedValueReplacer(
    keyPrefix = "{{",
    keySuffix = "}}",
    prepareKey = { keyFound -> keyFound.trim() }
)

fun String.replace(
    namedValueReplacer: NamedValueReplacer = defaultNamedValueReplacer,
    map: Map<String, Any?>
): String {

    if (isEmpty()) {
        return this
    }

    return namedValueReplacer.replacedNamed(this, map::containsKey, map::get)
}


fun String.replace(
    namedValueReplacer: NamedValueReplacer,
    pair: Pair<String, Any?>, vararg more: Pair<String, Any?>
): String {

    if (isEmpty()) {
        return this
    }

    if (more.isEmpty()) {
        return namedValueReplacer.replacedNamed(
            this,
            { key -> key == pair.first },
            { key -> if (pair.first == key) pair.second else null })
    }

    val map = pair.array(more).mapped()

    return replace(namedValueReplacer, map)
}


@Suppress("SpellCheckingInspection")
private val HEX_CHARS = "0123456789ABCDEF".toCharArray()

fun ByteArray.hex(): String {
    return fold(StringBuilder()) { stringBuilder, byte ->
        stringBuilder.apply {
            val octet = byte.toInt()
            val i0 = (octet and 0xF0) ushr 4
            val i1 = (octet and 0x0F)
            append(HEX_CHARS[i0])
            append(HEX_CHARS[i1])
        }
    }.toString()
}
