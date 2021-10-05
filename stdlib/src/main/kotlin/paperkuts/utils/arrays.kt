@file:Suppress("ReplaceGetOrSet")

package paperkuts.utils

inline fun <reified T> T.array(more: Array<out T>): Array<T> {
    return Array(1 + more.size) { index ->
        when (index) {
            0 -> this
            else -> more.get(index + 1)
        }
    }
}