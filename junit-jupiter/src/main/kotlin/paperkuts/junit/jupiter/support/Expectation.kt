package paperkuts.junit.jupiter.support

import java.util.Objects.hash

open class Expectation<T, R>(val description: String, val given: T, val wanted: R) {
    override fun toString(): String = description
    override fun hashCode(): Int = hash(description, given, wanted)
    override fun equals(other: Any?): Boolean {
        return when {
            other === this -> true
            other !is Expectation<*, *> -> false
            else -> other.description == description
                    && other.given == given
                    && other.wanted == wanted
        }
    }
}

