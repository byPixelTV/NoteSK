import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.2.0"
    id("io.papermc.paperweight.userdev") version "2.0.0-SNAPSHOT"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
}

val versionString = "1.1.1"

group = "de.bypixeltv"
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
    paperweight.paperDevBundle("1.21.4-R0.1-SNAPSHOT")

    bukkitLibrary("dev.jorel", "commandapi-bukkit-shade-mojang-mapped", "10.1.1")
    bukkitLibrary("dev.jorel", "commandapi-bukkit-kotlin", "10.1.1")
    bukkitLibrary("net.axay:kspigot:1.21.0")

    compileOnly("com.github.SkriptLang:Skript:2.12.0-pre1")
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
    main = "de.bypixeltv.notesk.Main"

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
