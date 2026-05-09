plugins {
    id("dev.tocraft.modmaster.fabric")
}

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft")}")
    implementation("net.fabricmc:fabric-loader:${property("fabric_loader")}")
    implementation("net.fabricmc.fabric-api:fabric-api:${property("fabric")}")

    commonJava(project(":common", "commonJava"))
    commonResources(project(":common", "commonResources"))

    // MixinExtras bundled with the mod
    include(implementation(annotationProcessor("io.github.llamalad7:mixinextras-fabric:${property("mixinextras_version")}")!!)!!)

    implementation("dev.tocraft:craftedcore-fabric:${property("craftedcore_version")}") {
        exclude(group = "net.fabricmc.fabric-api")
        exclude(group = "me.shedaniel.cloth")
    }
}

tasks.processResources {
    from("commonResources")
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
