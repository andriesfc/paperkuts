package paperkuts.junit.jupiter.config

/**
 * Some way for components to load config
 */
interface ConfigStore<S> {
    fun source(): S
    fun contains(property: String, componentClass: Class<*>): Boolean
    fun read(componentClass: Class<*>, property: String): String
    fun key(componentClass: Class<*>, property: String): String
    fun properties(componentClass: Class<*>): Set<String>
}