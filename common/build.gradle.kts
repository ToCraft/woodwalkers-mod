import dev.tocraft.gradle.preprocess.tasks.ApplyPreProcessTask

plugins {
    id("dev.tocraft.modmaster.common")
}

dependencies {
    modApi("dev.architectury:architectury:${rootProject.properties["architectury_version"]}")
}
tasks.named<ApplyPreProcessTask>("applyPreProcessJava") {
    removeComments = true
}
