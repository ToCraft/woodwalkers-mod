plugins {
    id("dev.tocraft.modmaster.common")
}

dependencies {
    modApi("dev.tocraft:craftedcore:${parent!!.name}-${rootProject.properties["craftedcore_version"]}")
}