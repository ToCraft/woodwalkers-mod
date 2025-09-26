plugins {
    id("dev.tocraft.modmaster.root") version("single-1.7")
}

allprojects {
    repositories {
        mavenLocal()
        mavenLocal() // Check local repository first

        // Minecraft Libraries repository with exclusive content filtering for LWJGL
        val minecraft = maven {
            name = "Minecraft Libraries"
            url = uri("https://libraries.minecraft.net")
            mavenContent {
                releasesOnly()
            }
        }

        // Set up exclusive content filtering for specific LWJGL dependencies that cause issues
        exclusiveContent {
            forRepositories(minecraft)
            filter {
                includeModule("org.lwjgl", "lwjgl-freetype")
                includeGroupAndSubgroups("com.mojang")
            }
        }

        mavenCentral() // Then Maven Central
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev/")
        maven("https://maven.minecraftforge.net/")
        maven("https://maven.parchmentmc.org")
        maven("https://maven.tocraft.dev/public")
    }
}

subprojects {
    // Standard configuration
    configurations.all {
        resolutionStrategy.preferProjectModules()
    }
}

ext {
    val modMeta = mutableMapOf<String, Any>()
    modMeta["minecraft_version"] = project.properties["minecraft"] as String
    modMeta["version"] = version
    modMeta["craftedcore_version"] = project.properties["craftedcore_version"] as String
    set("mod_meta", modMeta)
}
