plugins {
    id("net.fabricmc.fabric-loom")
    `java-library`
}

val javaVersion = (property("java") as String).toInt()

java {
    toolchain.languageVersion = JavaLanguageVersion.of(javaVersion)
    withSourcesJar()
}

// Resolve common sources from :common subproject
val commonJava: Configuration by configurations.creating { isCanBeResolved = true }
val commonResources: Configuration by configurations.creating { isCanBeResolved = true }
val commonDummy: Configuration by configurations.creating { isCanBeResolved = true }

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft")}")
    implementation("net.fabricmc:fabric-loader:${property("fabric_loader")}")
    implementation("net.fabricmc.fabric-api:fabric-api:${property("fabric")}")

    commonJava(project(":common", "commonJava"))
    commonResources(project(":common", "commonResources"))
    commonDummy(project(":common", "commonDummy"))

    implementation("dev.tocraft:craftedcore-fabric:${property("craftedcore_version")}") {
        exclude(group = "net.fabricmc.fabric-api")
        exclude(group = "me.shedaniel.cloth")
    }
}

// Include common sources in this compilation
tasks.compileJava {
    source(commonJava)
    source(commonDummy)
}
tasks.javadoc { source(commonJava) }

tasks.processResources {
    from(commonResources)
    val mcVersion = project.property("minecraft")
    val craftedcoreVersion = project.property("craftedcore_version")
    filesMatching("fabric.mod.json") {
        expand(mapOf(
            "version" to project.version,
            "minecraft_version" to mcVersion,
            "craftedcore_version" to craftedcoreVersion
        ))
    }
    outputs.upToDateWhen { false }
}

tasks.named<Jar>("sourcesJar") {
    from(commonJava)
    from(commonResources)
}
