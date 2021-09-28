@file:Suppress("UNCHECKED_CAST", "ReplaceGetOrSet")

import java.util.*

plugins {
    `java-base`
    `maven-publish`
}

val packingTasks = "publishing"

val packageSources: Jar by tasks.creating(Jar::class) {
    group = packingTasks
    archiveClassifier.set("sources")
    description = "Package source jar for publication"
    from(sourceSets.getByName("main").allSource)
}

val packageDokkaHtml: Jar by tasks.creating(Jar::class) {
    group = packingTasks
    archiveClassifier.set("dokka-html")
    description = "Package all dokka HTML docs into a single jar for publication"
    dependsOn(":${project.name}:dokkaHtml")
    from(buildDir.resolve("dokka/html"))
}

val packageJavaDocs: Jar by tasks.creating(Jar::class) {
    group = packingTasks
    archiveClassifier.set("javadoc")
    description = "Packages all javadocs into a single jar for publication"
    dependsOn(":${project.name}:dokkaJavadoc")
    from(buildDir.resolve("dokka/javadoc"))
}

val packageArtifacts: Task by tasks.creating {
    group = packingTasks
    description =
        "Convenience task which just package the sources, dokka html and javadocs into a jars."
    dependsOn(packageSources, packageDokkaHtml, packageJavaDocs)
}

fun String.unquoted() = trim('"', '\'')
fun String.prefixed(prefix: Char): String = if (startsWith(prefix)) this else "$prefix$this"
fun String.unformatted(): String = lineSequence().map(String::trim).joinToString(separator = " ")

fun Project.props(): Map<String, String?> {

    val properties = generateSequence(this, Project::getParent)
        .map { p -> p.file("project.properties").absoluteFile }
        .filter { f -> f.exists() }
        .map { f -> f.reader().use { rd -> Properties().also { props -> props.load(rd) } } }
        .toList()
        .foldRight(mutableMapOf<String, String?>()) { props, collected ->
            collected.apply {
                props.forEach { k, v -> put(k as String, v as String?) }
            }
        }

    return properties.toMap()
}

fun Map<String, String?>.withKeysStartingWith(prefix: String): Map<String, String?> {
    val pathPrefix = prefix.prefixed('.')
    return entries.asSequence()
        .filter { (path, _) -> path.startsWith(pathPrefix) }
        .map { (path, value) -> path to value }
        .toMap()
}

fun Map<String, String?>.records(keysOnPath: String): Map<String, Map<String, String?>> {
    val pathPrefix = keysOnPath.prefixed('.')
    val records = mutableMapOf<String, MutableMap<String, String?>>()
    for ((path, value) in entries) {
        if (!path.startsWith(pathPrefix)) continue
        val idStartIndex = path.indexOf('.')
        val fieldStartIndex = path.indexOf('.', startIndex = pathPrefix.length)
        val id = when (fieldStartIndex) {
            -1 -> path.substring(idStartIndex + 1)
            else -> path.substring(
                idStartIndex + 1,
                fieldStartIndex - 1
            )
        }
        val record: MutableMap<String, String?> = records.getOrPut(id, ::LinkedHashMap)
        if (fieldStartIndex == -1) continue
        val field = path.substring(idStartIndex + 1, fieldStartIndex)
        record[field] = value
    }
    return records.toMap()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifact(packageDokkaHtml)
            artifact(packageJavaDocs)
            artifact(packageSources)
            from(components.get("java"))
            val props = props()
            artifactId = "${rootProject.name}-${props.get("project.name") ?: project.name}"
            pom.inceptionYear.set(props.get("project.inceptionYear"))
            pom.description.set(props.get("project.description")?.unquoted())
            pom.licenses {
                props.records("project.license").forEach { (licenseId, licence) ->
                    license {
                        name.set(licenseId)
                        url.set(licence.get("url"))
                        distribution.set(licence.get("distribution"))
                        comments.set(licence.get("comments")?.unformatted()?.unquoted())
                    }
                }
            }
            pom.developers {
                props.records("project.developer").forEach { (developerId, developer) ->
                    developer {
                        id.set(developerId)
                        name.set(developer.get("name"))
                        email.set(developer.get("email"))
                        timezone.set(developer.get("timezone"))
                        organization.set(developer.get("org"))
                        organizationUrl.set(developer.get("org.url"))
                        roles.set(
                            developer.entries
                                .filter { e -> e.key.startsWith("role") }
                                .mapNotNull { e -> e.value })
                    }
                }
            }
        }
    }
}

