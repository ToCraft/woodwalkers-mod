plugins {
    id("dev.tocraft.modmaster.common")
}

dependencies {
    modApi("dev.architectury:architectury:${rootProject.properties["architectury_version"]}")
}
