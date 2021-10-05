package paperkuts.collections

fun <L, T> L.of(vararg items: T): L where L : MutableList<T> {
    return apply {
        addAll(items)
    }
}

fun <L,T> L.list(): List<T> where L: MutableList<T> = toList()