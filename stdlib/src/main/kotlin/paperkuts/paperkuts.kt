package paperkuts

fun <A, B> pair(a: A, b: B): Pair<A, B> = Pair(a, b)

fun Throwable.trace(): Sequence<Throwable> {
    return generateSequence(this, Throwable::cause)
}

fun Throwable.rootCause(): Throwable? = trace().lastOrNull()

inline fun <reified X : Throwable> Throwable.causedBy(): X? = trace().lastOrNull { it is X } as X?


private const val whitespace = " "

fun String.unformatted(): String {
    if (isEmpty()) {
        return this
    }
    return lineSequence().map(String::trim).joinToString(separator = whitespace)
}

fun String.prefixed(prefix: Char): String = if (startsWith(prefix)) this else "$prefix$this"
fun String.prefixed(prefix: String): String = if (startsWith(prefix)) this else "$prefix$this"

inline fun <reified T> array(vararg items: T): Array<T> = arrayOf(*items)

inline fun <A, B, A1> Pair<A, B>.map(a1: (A) -> A1): Pair<A1, B> = pair(a1(first), second)
inline fun <A, B, B1> Pair<A, B>.mapSecond(b1: (B) -> B1): Pair<A, B1> = pair(first, b1(second))
inline fun <A, B, A1, B1> Pair<A, B>.map(a1: (A) -> A1, b1: (B) -> B1): Pair<A1, B1> =
    pair(a1(first), b1(second))