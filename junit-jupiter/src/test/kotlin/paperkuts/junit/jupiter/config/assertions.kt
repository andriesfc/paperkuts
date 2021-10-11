package paperkuts.junit.jupiter.config

import assertk.Assert
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isSuccess
import assertk.assertions.isTrue

fun Assert<ConfigStore<*>>.isConfiguredFor(
    componentClass: Class<*>,
    property: String,
) {
    given { store ->
        assertk.assertThat { store.contains(property, componentClass) }
            .isSuccess()
            .isTrue()
    }
}

fun Assert<ConfigStore<*>>.isNotConfiguredFor(
    componentClass: Class<*>,
    property: String,
) {
    given { store ->
        assertk.assertThat { store.contains(property, componentClass) }
            .isSuccess()
            .isFalse()
    }
}

fun Assert<ConfigStore<*>>.key(componentClass: Class<*>, property: String): Assert<String> {
    return transform("key") { it.key(componentClass, property) }
}

fun Assert<ConfigStore<*>>.isConfiguredAs(
    componentClass: Class<*>,
    property: String,
    propertyValue: String,
) {
    given { configStore ->
        assertk.assertThat {
            configStore.read(componentClass, property)
        }.isSuccess().isEqualTo(propertyValue)
    }
}

fun Assert<ConfigStore<*>>.configuredWith(componentClass: Class<*>, property: String): Assert<String?> {
    return transform { store -> store.read(componentClass, property) }
}