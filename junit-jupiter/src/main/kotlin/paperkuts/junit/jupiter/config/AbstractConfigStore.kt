package paperkuts.junit.jupiter.config

abstract class AbstractConfigStore<S> : ConfigStore<S> {

    override fun key(componentClass: Class<*>, property: String): String {
        return StringBuilder()
            .appendNameParts(
                configNamespace(componentClass),
                componentClass.simpleName,
                property)
            .toString()
    }

    protected open fun configNamespace(componentClass: Class<*>): String {
        return componentClass.configNamespace()
    }

    final override fun contains(property: String, componentClass: Class<*>): Boolean {
        return contains(key(componentClass, property))
    }

    final override fun read(componentClass: Class<*>, property: String): String {
        return get(key(componentClass, property))
    }

    protected abstract fun contains(key: String): Boolean
    protected abstract fun get(key: String): String

    companion object {

        private const val NAME_PART_DELIMITER = '.'

        private fun StringBuilder.appendNameParts(
            part: String,
            vararg parts: String,
        ): StringBuilder {

            append(part.trim(NAME_PART_DELIMITER))

            if (parts.isNotEmpty()) {
                append(NAME_PART_DELIMITER)
                parts.joinTo(
                    this,
                    separator = "$NAME_PART_DELIMITER",
                    transform = { it.trim(NAME_PART_DELIMITER) }
                )
            }

            return this
        }

    }
}