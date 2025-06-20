plugins {
    id("dev.tocraft.modmaster.common")
}

dependencies {
    modApi("dev.tocraft:craftedcore:${rootProject.properties["minecraft"]}-${rootProject.properties["craftedcore_version"]}") {
        exclude("me.shedaniel.cloth")
    }
}