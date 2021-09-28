import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    id("org.jetbrains.dokka")
    `java-library`
}


if (file("module.md").exists()) {
    tasks.withType<DokkaTask>() {
        dokkaSourceSets {
            named("main") {
                moduleName.set(project.name)
                includes.from("module.md")
                jdkVersion.set(java.toolchain.languageVersion.get().asInt())
                sourceLink {
                    localDirectory.set(file("src/main/kotlin"))
                }
            }
        }
    }
}

