plugins {
    id("dev.tocraft.modmaster.neoforge")
}

tasks.withType<ProcessResources> {
    @Suppress("UNCHECKED_CAST") val modMeta = parent!!.ext["mod_meta"]!! as Map<String, Any>

    filesMatching("META-INF/mods.toml") {
        expand(modMeta)
    }

    filesMatching("META-INF/neoforge.mods.toml") {
        expand(modMeta)
    }


    outputs.upToDateWhen { false }
}

dependencies {
    compileOnly(annotationProcessor("io.github.llamalad7:mixinextras-common:${rootProject.properties["mixinextras_version"]}")!!)
    implementation(include("io.github.llamalad7:mixinextras-neoforge:${rootProject.properties["mixinextras_version"]}")!!)
    modApi("dev.tocraft:craftedcore-neoforge:${parent!!.name}-${rootProject.properties["craftedcore_version"]}") {
        exclude("me.shedaniel.cloth")
    }
}