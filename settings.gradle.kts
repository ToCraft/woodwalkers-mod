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

plugins {
    id("dev.tocraft.modmaster.settings") version "1.2"
}

rootProject.name = "walkers"
