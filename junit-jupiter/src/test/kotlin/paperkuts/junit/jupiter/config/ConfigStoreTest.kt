@file:Suppress("ReplacePutWithAssignment")

package paperkuts.junit.jupiter.config

import assertk.all
import assertk.assertAll
import assertk.assertThat
import assertk.assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.*


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class ConfigStoreTest {

    private lateinit var configStore: ConfigStore<MutableMap<String, String>>
    private lateinit var componentClass: Class<*>

    interface TestComponent

    @BeforeAll
    fun setupAllTests() {
        configStore = buildConfigStoreOfSource(
            source = mutableMapOf(),
            contains = { key: String -> containsKey(key) },
            get = { key: String -> get(key) ?: throw NoSuchElementException(key) },
            collectKeys = { collect, predicate -> keys.filter(predicate).forEach(collect) }
        )
    }

    @BeforeEach
    fun beforeEachTest() {
        configStore.source().clear()
        componentClass = TestComponent::class.java
    }

    @Test
    fun configStoreShouldKnowIfAPropertyIsStored() {
        val unknownProperty = "property-${UUID.randomUUID()}"
        assertThat(configStore).isNotConfiguredFor(componentClass, unknownProperty)
    }

    @Test
    fun keyShouldContainTestComponentAndProperty() {
        val property = "testProperty1"
        assertThat(configStore).key(componentClass, property).all {
            contains(componentClass.simpleName)
            contains(property)
        }
    }

    @Test
    fun loadingNonExistingKeyShouldFail() {
        val unknownProperty = "property-${UUID.randomUUID()}"
        assertThat { configStore.read(TestComponent::class.java, unknownProperty) }
            .isFailure()
            .isInstanceOf(NoSuchElementException::class)
            .message().isNotNull()
            .contains(unknownProperty)
    }

    @Test
    fun loadingExistingPropertyShouldNotFail() {
        val property = "testProperty"
        val propertyValue = "expected"
        configurePropertyAs(componentClass, property, propertyValue)
        assertThat(configStore).isConfiguredFor(componentClass, property)
        assertThat(configStore).isConfiguredAs(componentClass, property, propertyValue)
    }

    @Test
    fun samePropertyShouldBeStorableByDifferentComponents() {

        class AnotherComponent

        val anotherComponentClass = AnotherComponent::class.java
        val anotherComponentPropertyValue = "anotherValue"
        val property = "property"
        val componentPropertyValue = "componentValue"

        val k1 = configurePropertyAs(
            anotherComponentClass,
            property,
            anotherComponentPropertyValue
        )

        val k2 = configurePropertyAs(
            componentClass,
            property,
            componentPropertyValue
        )

        assertAll {

            assertThat(k1)
                .isNotEqualTo(k2)

            assertThat(configStore)
                .configuredWith(componentClass, property).isNotNull()
                .isNotEqualTo(anotherComponentPropertyValue)

            assertThat(configStore)
                .configuredWith(anotherComponentClass, property).isNotNull()
                .isNotEqualTo(componentPropertyValue)
        }

    }


    @Suppress("SameParameterValue")
    private fun configurePropertyAs(
        componentClass: Class<*>,
        property: String,
        value: String,
    ): String {
        val key = configStore.key(componentClass, property)
        configStore.source().put(key, value)
        println("stored : $key => [$value]")
        return key
    }

}


