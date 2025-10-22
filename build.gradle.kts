import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.2.20"
    id("io.papermc.paperweight.userdev") version "2.0.0-SNAPSHOT"
    id("de.eldoria.plugin-yml.bukkit") version "0.8.0"
}

fun getLatestTag(): String {
    try {
        // fetch all tags (remote + local)
        ProcessBuilder("git", "fetch", "--tags")
            .redirectErrorStream(true)
            .start()
            .apply {
                inputStream.bufferedReader().use { it.readText() }
                waitFor()
            }

        // get current branch
        val branch = ProcessBuilder("git", "rev-parse", "--abbrev-ref", "HEAD")
            .redirectErrorStream(true)
            .start()
            .inputStream
            .bufferedReader()
            .use { it.readText().trim() }

        // get latest tag
        val tagProcess = ProcessBuilder("git", "describe", "--tags", "--abbrev=0")
            .redirectErrorStream(true)
            .start()

        val rawTag = tagProcess.inputStream.bufferedReader().use { it.readText().trim() }
        tagProcess.waitFor()

        if (rawTag.isEmpty()) return "unknown"

        val tag = rawTag.removePrefix("v")

        return if (branch == "release") {
            tag
        } else {
            // get short commit hash
            val commitProcess = ProcessBuilder("git", "rev-parse", "--short", "HEAD")
                .redirectErrorStream(true)
                .start()
            val commit = commitProcess.inputStream.bufferedReader().use { it.readText().trim() }
            commitProcess.waitFor()

            "$tag+$commit"
        }
    } catch (e: Exception) {
        return "unknown"
    }
}

val versionString = getLatestTag()

group = "dev.bypixel"
version = versionString

repositories {
    mavenCentral()

    maven("https://jitpack.io")

    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        url = uri("https://repo.skriptlang.org/releases")
    }
}

dependencies {
    paperweight.paperDevBundle("1.21.10-R0.1-SNAPSHOT")

    bukkitLibrary("dev.jorel", "commandapi-bukkit-shade-mojang-mapped", "10.1.2")
    bukkitLibrary("dev.jorel", "commandapi-bukkit-kotlin", "10.1.2")
    bukkitLibrary("net.axay:kspigot:1.21.0")

    compileOnly("com.github.SkriptLang:Skript:2.13.0")
    compileOnly("com.github.koca2000:NoteBlockAPI:1.6.3")

}

sourceSets {
    getByName("main") {
        java {
            srcDir("src/main/java")
        }
        kotlin {
            srcDir("src/main/kotlin")
        }
    }
}

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

tasks {
    compileJava {
        options.encoding = "UTF-8"
        options.release.set(21)
    }

    compileKotlin {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
    }
}

bukkit {
    main = "dev.bypixel.notesk.Main"

    version = versionString

    foliaSupported = false

    apiVersion = "1.21"

    authors = listOf("byPixelTV")

    website = "https://github.com/byPixelTV/NoteSK"

    description = "A Skript-Addon to player .nbs files using NoteBlockAPI."

    depend = listOf("NoteBlockAPI", "Skript")

    prefix = "NoteSK"
}

kotlin {
    jvmToolchain(21)
}
