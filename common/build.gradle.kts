plugins {
    id("dev.tocraft.modmaster.common")
}

dependencies {
    implementation(annotationProcessor("io.github.llamalad7:mixinextras-common:${rootProject.properties["mixinextras_version"]}")!!)
    modApi("dev.tocraft:craftedcore:${parent!!.name}-${rootProject.properties["craftedcore_version"]}") {
        exclude("me.shedaniel.cloth")
    }
}