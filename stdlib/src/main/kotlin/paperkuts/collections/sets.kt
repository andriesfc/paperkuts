package paperkuts.collections


fun <S, T> S.of(vararg items: T): S where S : MutableSet<T> {
    return apply {
        addAll(items)
    }
}

fun <T> MutableSet<T>.set(): Set<T> = toSet()