pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev/")
        maven("https://maven.minecraftforge.net/")
        maven("https://maven.parchmentmc.org")
        maven("https://maven.tocraft.dev/public")
        gradlePluginPortal()
    }
}

// set to only use one minecraft version
val minecraft = ""

file("props").listFiles()?.forEach {
    var forcedVersion : String? = startParameter.projectProperties["minecraft"]
    if (forcedVersion == null) {
        forcedVersion = minecraft
    }
    if (forcedVersion.isNotBlank()) {
        if (!it.name.startsWith(forcedVersion)) {
            return@forEach
        }
    }

    val props = it.readLines()
    var foundFabric = false
    for (line in props) {
        if (line.startsWith("fabric")) {
            foundFabric = true
            break
        }
    }
    var foundForge = false
    for (line in props) {
        if (line.startsWith("forge")) {
            foundForge = true
            break
        }
    }
    var foundNeoForge = false
    for (line in props) {
        if (line.startsWith("neoforge")) {
            foundNeoForge = true
            break
        }
    }

    val version = it.name.replace(".properties", "")
    include(":$version")
    project(":$version").apply {
        projectDir = file("versions/$version")
        buildFileName = "../../build.gradle"
    }

    include(":$version:common")
    project(":$version:common").apply {
        projectDir = file("common")
    }

    if (foundFabric) {
        include(":$version:fabric")
        project(":$version:fabric").apply {
            projectDir = file("fabric")
        }
    }
    if (foundForge) {
        include(":$version:forge")
        project(":$version:forge").apply {
            projectDir = file("forge")
        }
    }
    if (foundNeoForge) {
        include(":$version:neoforge")
        project(":$version:neoforge").apply {
            projectDir = file("neoforge")
        }
    }
}

rootProject.name = "walkers"
rootProject.buildFileName = "root.gradle.kts"