plugins {
    id("net.fabricmc.fabric-loom") version "1.15.5" apply false
    id("net.neoforged.moddev") version "2.0.141" apply false
}

allprojects {
    group = property("maven_group") as String
    version = property("mod_version") as String
}

subprojects {
    apply(plugin = "maven-publish")

    afterEvaluate {
        configure<PublishingExtension> {
            publications {
                create<MavenPublication>("mavenJava") {
                    from(components["java"])
                }
            }
        }
    }

    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.neoforged.net/releases/")
        maven("https://maven.terraformersmc.com/releases/")
        maven("https://maven.shedaniel.me/")
        maven {
            name = "Minecraft Libraries"
            url = uri("https://libraries.minecraft.net")
        }
    }
}
