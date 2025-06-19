import dev.tocraft.gradle.preprocess.tasks.ApplyPreProcessTask

plugins {
    id("dev.tocraft.modmaster.version")
}

ext {
    val modMeta = mutableMapOf<String, Any>()
    modMeta["minecraft_version"] = project.name
    modMeta["version"] = version
    modMeta["architectury_version"] = project.properties["architectury_version"] as String
    set("mod_meta", modMeta)
}


