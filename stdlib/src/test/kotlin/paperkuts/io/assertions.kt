package paperkuts.io

import assertk.Assert
import assertk.assertions.containsAll
import assertk.assertions.isEqualTo
import assertk.assertions.prop
import kotlinx.serialization.Serializable
import paperkuts.nio.file.*

@Serializable
data class PartExpectation(
    val parent: String?,
    val name: String,
    val extension: String?,
    val baseName: String,
)

fun <T> Assert<Parts<T>>.isAsExpected(expected: PartExpectation) {
    prop(Parts<T>::name).isEqualTo(expected.name)
    prop(Parts<T>::parent).transform { parent -> parent?.toString() }.isEqualTo(expected.parent)
    prop(Parts<T>::baseName).isEqualTo(expected.baseName)
    prop(Parts<T>::extension).isEqualTo(expected.extension)
}

fun <T> Assert<Parts<T>>.isDestructedTo(expected: Array<out String?>) {
    transform { parts ->
        listOf(
            parts.component1()?.toString(),
            parts.component2(),
            parts.component3(),
            parts.component4()
        )
    }.containsAll(*expected)
}
