plugins {
    id("dev.tocraft.modmaster.root") version ("2.4-SNAPSHOT")
}

subprojects {
    repositories {
        mavenLocal()
        maven("https://maven.fabricmc.net/") // fabric api
        maven("https://maven.terraformersmc.com/releases/") // mod menu mod
        maven("https://maven.shedaniel.me/") // cloth config
        maven {
            name = "Minecraft Libraries"
            url = uri("https://libraries.minecraft.net")
        }
    }
}
