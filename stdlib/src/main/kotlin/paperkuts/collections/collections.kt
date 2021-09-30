package paperkuts.collections

abstract class Builder<T> {

    protected abstract fun emit(): T

    abstract fun reset()

    abstract fun discard()

    fun build(): T = emit().also { reset() }
}


