@file:Suppress("ReplacePutWithAssignment")

package paperkuts.collections

fun <M, K, V> M.of(vararg pairs: Pair<K, V>): M where M : MutableMap<K, V> {
    return apply {
        putAll(pairs)
    }
}

fun <K, V> MutableMap<K, V>.mapped(): Map<K, V> = toMap()

fun <K, V> Array<Pair<K, V>>.mapped() = HashMap<K, V>().of(*this).mapped()