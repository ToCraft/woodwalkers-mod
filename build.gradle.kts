plugins {
    id("dev.tocraft.modmaster.root") version("single-1.4")
}

ext {
    val modMeta = mutableMapOf<String, Any>()
    modMeta["minecraft_version"] = project.properties["minecraft"] as String
    modMeta["version"] = version
    modMeta["architectury_version"] = project.properties["architectury_version"] as String
    set("mod_meta", modMeta)
}


