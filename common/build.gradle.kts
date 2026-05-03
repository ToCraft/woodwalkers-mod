plugins {
    `java-library`
    id("net.neoforged.moddev")
}

val javaVersion = (property("java") as String).toInt()

java {
    toolchain.languageVersion = JavaLanguageVersion.of(javaVersion)
    withSourcesJar()
}

neoForge {
    neoFormVersion = property("neoform_version") as String
}

// Include WAILA stub sources so WalkersWailaPlugin compiles without a real WAILA dep
sourceSets.main.configure {
    java.srcDir("src/dummy/java")
}

// Expose common sources as consumable artifacts for loader subprojects
val commonJava: Configuration by configurations.creating {
    isCanBeResolved = false
    isCanBeConsumed = true
}
val commonResources: Configuration by configurations.creating {
    isCanBeResolved = false
    isCanBeConsumed = true
}
val commonDummy: Configuration by configurations.creating {
    isCanBeResolved = false
    isCanBeConsumed = true
}

artifacts {
    add("commonJava", file("src/main/java"))
    add("commonResources", sourceSets.main.get().resources.sourceDirectories.singleFile)
    add("commonDummy", file("src/dummy/java"))
}

dependencies {
    compileOnly("org.spongepowered:mixin:0.8.5")
    compileOnly("io.github.llamalad7:mixinextras-common:${property("mixinextras_version")}")
    annotationProcessor("io.github.llamalad7:mixinextras-common:${property("mixinextras_version")}")
    compileOnly("net.fabricmc:fabric-loader:${property("fabric_loader")}")

    compileOnly("dev.tocraft:craftedcore:${property("craftedcore_version")}") {
        exclude(group = "me.shedaniel.cloth")
    }
    compileOnly("org.ow2.asm:asm:9.8")
}
