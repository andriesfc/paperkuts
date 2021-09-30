package paperkuts.collections

sealed class MapBuilder<K, V> : Builder<Map<K, V>>() {
    abstract fun put(k: K, v: V)
}

private class MapBuilderImpl<K, V>(private val factory: () -> MutableMap<K, V>) :
    MapBuilder<K, V>() {

    private lateinit var map: MutableMap<K, V>

    init {
        reset()
    }

    override fun emit(): Map<K, V> = map.toMap()

    override fun reset() {
        map = factory()
    }

    override fun discard() {
        map.clear()
    }

    override fun put(k: K, v: V) {
        map.put(k, v)
    }

}