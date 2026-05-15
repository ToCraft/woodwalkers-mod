plugins {
    id("dev.tocraft.modmaster.common")
}

dependencies {
    compileOnly("org.spongepowered:mixin:0.8.5")
    compileOnly("io.github.llamalad7:mixinextras-common:${property("mixinextras_version")}")
    annotationProcessor("io.github.llamalad7:mixinextras-common:${property("mixinextras_version")}")

    compileOnly("dev.tocraft:craftedcore:${property("craftedcore_version")}") {
        exclude(group = "me.shedaniel.cloth")
    }
    compileOnly("org.ow2.asm:asm:9.8")
}