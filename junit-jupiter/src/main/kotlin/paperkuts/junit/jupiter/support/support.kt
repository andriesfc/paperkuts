package paperkuts.junit.jupiter.support

private const val UNDERSCORE = '_'
private const val DASH = '-'
private const val DOT = '.'
private const val SINGLE_SPACE = " "
private val delimiting = setOf(UNDERSCORE, DASH, DOT)::contains
private val caseDelimiting = Char::isUpperCase

private inline fun StringBuilder.charIs(index: Int, predicate: (Char) -> Boolean): Boolean {
    return predicate(get(index))
}

private fun StringBuilder.replaceAllCharAtWith(
    from: Int,
    replacement: String,
) {
    val replacing = get(from)
    while (from < length && get(from) == replacing) {
        deleteCharAt(from)
    }
    insert(from, replacement)
}

fun nameToSentence(name: String): String {

    if (name.isBlank()) {
        return name
    }

    val sb = StringBuilder(name)
    var i = 0

    while (true) {

        if (i == sb.length) {
            break
        }

        if (sb.charIs(i, delimiting)) {
            sb.replaceAllCharAtWith(i, SINGLE_SPACE)
        } else if (i > 0 && sb.charIs(i, caseDelimiting)) {
            sb[i] = sb[i].lowercaseChar()
            sb.insert(i, SINGLE_SPACE)
        }

        i += 1
    }

    if (sb[0].isLowerCase()) {
        sb.setCharAt(0, sb[0].uppercaseChar())
    }

    return sb.toString()
}
