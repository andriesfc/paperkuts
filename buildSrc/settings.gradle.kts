pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven(uri("https://plugins.gradle.org/m2/"))
    }
    plugins {
        kotlin("jvm").version("1.5.31")
        id("org.jetbrains.kotlin.plugin.serialization").version("1.5.31")
    }
}