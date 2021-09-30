package paperkuts.text

fun regex(pattern: String, vararg options: RegexOption): Regex {
    return when {
        options.isEmpty() -> Regex(pattern)
        else -> Regex(pattern, options.toSet())
    }
}

