package paperkuts.collections

sealed class ListBuilder<T> : Builder<List<T>>() {
    abstract fun add(item: T)
    abstract fun add(item: T, vararg more: T)
    abstract fun size(): Int
}

fun <T> buildList(
    factory: () -> MutableList<T> = { mutableListOf<T>() },
    building: ListBuilder<T>.() -> Unit
): List<T> {
    return ListBuilderImp(factory).run {
        building()
        build()
    }
}

private class ListBuilderImp<T>(private val factory: () -> MutableList<T>) : ListBuilder<T>() {

    private var building = factory()

    override fun reset() {
        building = factory()
    }

    override fun emit(): List<T> = building.toList()

    override fun discard() = building.clear()

    override fun add(item: T) {
        building.add(item)
    }

    override fun size(): Int = building.size

    override fun add(item: T, vararg more: T) {
        building.add(item)
        building.addAll(more)
    }
}