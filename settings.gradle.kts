pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/") // fabric loom
        maven("https://maven.neoforged.net/releases/") // neoforge mod dev
        maven("https://maven.tocraft.dev/public") // mod master
        gradlePluginPortal() // publishing plugins (CFGradle, Minotaur, Schoomp)
    }
}

rootProject.name = "walkers"
include("common")
include("fabric")
include("neoforge")
