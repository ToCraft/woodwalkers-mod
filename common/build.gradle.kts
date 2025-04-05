import dev.tocraft.gradle.preprocess.tasks.ApplyPreProcessTask

plugins {
    id("dev.tocraft.modmaster.common")
}

dependencies {
    modApi("dev.tocraft:craftedcore:${parent!!.name}-${rootProject.properties["craftedcore_version"]}") {
        exclude("me.shedaniel.cloth")
    }
}
tasks.named<ApplyPreProcessTask>("applyPreProcessJava") {
    removeComments = true
}