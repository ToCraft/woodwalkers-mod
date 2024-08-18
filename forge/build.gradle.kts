plugins {
    id("dev.tocraft.modmaster.forge")
}

tasks.withType<ProcessResources> {
    @Suppress("UNCHECKED_CAST")val modMeta = parent!!.ext["mod_meta"]!! as Map<String, Any>

    filesMatching("META-INF/mods.toml") {
        expand(modMeta)
    }

    outputs.upToDateWhen { false }
}

loom {
    forge {
        mixinConfigs.add("skinshifter.mixins.json")
    }
}

dependencies {
    modApi("dev.tocraft:craftedcore-forge:${parent!!.name}-${rootProject.properties["craftedcore_version"]}")
}