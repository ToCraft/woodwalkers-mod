import java.util.*

plugins {
    id("dev.tocraft.modmaster.common")
}

val ccversion = (parent!!.ext["props"] as Properties)["craftedcore"] as String

dependencies {
    modApi("dev.tocraft:craftedcore:${ccversion}-${rootProject.properties["craftedcore_version"]}") {
        exclude("me.shedaniel.cloth")
    }
}
