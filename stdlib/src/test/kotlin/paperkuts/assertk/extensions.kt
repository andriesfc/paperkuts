package paperkuts.assertk

import assertk.Assert

fun <T> Assert<T>.peek(peek: (T) -> Unit): Assert<T> = apply { given(peek) }