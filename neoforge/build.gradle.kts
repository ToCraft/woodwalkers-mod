plugins {
    id("net.neoforged.moddev")
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

neoForge {
    version = property("neoforge") as String
}

dependencies {
    commonJava(project(":common", "commonJava"))
    commonResources(project(":common", "commonResources"))
    commonDummy(project(":common", "commonDummy"))

    // Needed to compile common sources that use @Environment(EnvType.CLIENT)
    compileOnly("net.fabricmc:fabric-loader:${property("fabric_loader")}")

    implementation("dev.tocraft:craftedcore-neoforge:${property("craftedcore_version")}") {
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
    filesMatching(listOf("META-INF/neoforge.mods.toml", "META-INF/mods.toml")) {
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
