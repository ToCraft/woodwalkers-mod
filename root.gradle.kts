import com.diluv.schoomp.Webhook
import com.diluv.schoomp.message.Message
import com.diluv.schoomp.message.embed.Embed
import java.io.FileWriter
import java.io.IOException

buildscript {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath("com.diluv.schoomp:Schoomp:1.2.6")
    }
}

plugins {
    id("architectury-plugin") version "3.4-SNAPSHOT"
    id("dev.architectury.loom") version "1.7-SNAPSHOT" apply false
}

allprojects {
    repositories {
        // Add repositories to retrieve artifacts from in here.
        // You should only use this when depending on other mods because
        // Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
        // See https://docs.gradle.org/current/userguide/declaring_repositories.html
        // for more information about repositories.
        maven("https://maven.parchmentmc.org")
        maven("https://maven.neoforged.net/releases/")
    }
}

ext {
    set("releaseChangelog", releaseChangelog(1))
}

fun releaseChangelog(versions : Int): String {
    try {
        var changelog = ""
        var match = 0
        var previousIT = ""
        rootProject.file("CHANGELOG.md").forEachLine {
            if (it.contains("================")) {
                match++
            }
            if (match <= versions) {
                changelog += previousIT + "\n"
            }
            previousIT = it
            return@forEachLine
        }
        return changelog + "\n\n"
    } catch (exception : Exception) {
        return "${rootProject.properties["archives_base_name"]} ${rootProject.properties["mod_version"]}\n==========\nThere was an error generating the changelog" + exception.localizedMessage
    }
}

//Splits the changelog into multiple parts if they get bigger than discords embed field size (1024)
fun discordChangelog(): List<String> {
    val changelog = releaseChangelog(1)
    val res = mutableListOf<String>()
    if (changelog.length < 1024) {
        res.add(changelog)
        return res
    }
    var temp = ""
    changelog.split("\n").forEach {
        var it2 = it + "\n"
        if ((temp.length + it2.length) >= 1024) {
            res.add(temp)
            temp = it2
        } else
            temp += it2
    }
    res.add(temp)
    return res
}

// based on the code by Flemmli97
tasks.register("discordRelease") {
    doLast {
        try {
            val webhook = Webhook(System.getenv("DISCORD_WEB_HOOK"), "${project.name} Upload")

            val message = Message()
            message.setUsername("Mod Release")
            var content = "${project.name} ${rootProject.properties["mod_version"]} has been released!"
            if (rootProject.hasProperty("ping_role")) {
                content = "<@&${rootProject.properties["ping_role"]}> " + content
            }
            message.setContent(content)
            message.setAvatarUrl("https://avatars.githubusercontent.com/u/38883321")

            val embed = Embed()

            val changelog = discordChangelog()
            if (changelog.size == 1)
                embed.addField("Change Log", "```md\n${changelog[0]}```", false)
            else
                changelog.forEach {
                    embed.addField("Change Log", "```md\n${it}```", false)
                }
            embed.setColor(0xFF8000)
            message.addEmbed(embed)

            webhook.sendMessage(message)
        }

        catch (ignored : IOException) {
            println("Failed to push to the Discord webhook.")
        }
        println("Send Changelog to Discord.")
    }
}

tasks.register("extractNewestChangelog") {
    val fileName = "extracted.CHANGELOG.md";
    // delete file if exists
    delete(fileName)
    doLast {
        // write changelog
        val fw = FileWriter(fileName);
        fw.write(releaseChangelog(1));
        fw.close()
        println("Extracted newest Changelog to \"extracted.CHANGELOG.md\"")
    }
}
