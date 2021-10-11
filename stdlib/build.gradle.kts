plugins {
    id("project.kotlin-library-conventions")
    id("org.jetbrains.kotlin.plugin.serialization").version("1.5.31")
}

dependencies {
    testImplementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")
    testImplementation(project(":junit-jupiter"))
}
