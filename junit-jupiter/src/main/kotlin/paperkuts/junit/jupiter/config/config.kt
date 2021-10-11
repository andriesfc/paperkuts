package paperkuts.junit.jupiter.config

inline fun <S> buildConfigStoreOfSource(
    source: S,
    crossinline contains: S.(key: String) -> Boolean,
    crossinline get: S.(key: String) -> String,
    crossinline collectKeys: S.(collect: (String) -> Unit, predicate: (key: String) -> Boolean) -> Unit,
): ConfigStore<S> {
    return object : AbstractConfigStore<S>() {
        override fun source(): S = source
        override fun contains(key: String): Boolean = source().contains(key)
        override fun get(key: String): String = source().get(key)
        override fun properties(componentClass: Class<*>): Set<String> {
            val namespace = configNamespace(componentClass)
            val collected = mutableSetOf<String>()
            source().collectKeys(
                { key -> collected += key.substring(namespace.length + 1) },
                { key -> key.startsWith(namespace) && key.length > namespace.length }
            )
            return collected.toSet()
        }
    }
}

val configPackage: Package = run {
    val anon = object {}
    anon.javaClass.`package`
}

fun Class<*>.configNamespace(): String {
    return when {
        (`package` == null
                || `package` == configPackage
                || packageName.startsWith(configPackage.name)) -> configPackage.name
        else -> packageName
    }
}
