package paperkuts.testing

fun <K, V> Map<K, V>.arguments(): List<Array<Any?>> = entries.map { (k, v) -> arrayOf(k, v) }
