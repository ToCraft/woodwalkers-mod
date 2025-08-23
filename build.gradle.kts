plugins {
    id("dev.tocraft.modmaster.root") version("single-1.7")
}

ext {
    val modMeta = mutableMapOf<String, Any>()
    modMeta["minecraft_version"] = project.properties["minecraft"] as String
    modMeta["version"] = version
    modMeta["craftedcore_version"] = project.properties["craftedcore_version"] as String
    set("mod_meta", modMeta)
}
