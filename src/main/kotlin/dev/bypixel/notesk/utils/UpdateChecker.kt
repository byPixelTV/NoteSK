package dev.bypixel.notesk.utils

import ch.njol.skript.util.Version
import com.google.gson.Gson
import com.google.gson.JsonObject
import dev.bypixel.notesk.Main
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.event.Listener
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URI
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

/*
    This got ported into Kotlin by @byPixelTV and was originally written in Java by ShaneBeee
    You can find this code at https://github.com/ShaneBeee/SkBee/blob/master/src/main/java/com/shanebeestudios/skbee/api/util/UpdateChecker.java
    Checkout https://github.com/ShaneBeee/SkBee
*/

class UpdateChecker(private val plugin: Main) : Listener {

    companion object {
        private var UPDATE_VERSION: Version? = null
        private val miniMessages = MiniMessage.miniMessage()

        fun checkForUpdate(pluginVersion: String) {
            Main.INSTANCE.server.consoleSender.sendMessage(miniMessages.deserialize("<grey>[<dark_purple>NoteSK</dark_purple>]</grey> Checking for updates..."))
            getLatestReleaseVersion { version ->
                val plugVer = Version(pluginVersion)
                val curVer = Version(version)
                if (curVer <= plugVer) {
                    Main.INSTANCE.server.consoleSender.sendMessage(miniMessages.deserialize("<grey>[<dark_purple>NoteSK</dark_purple>]</grey> <green>The plugin is up to date!</green>"))
                } else {
                    Main.INSTANCE.server.consoleSender.sendMessage(miniMessages.deserialize("<grey>[<dark_purple>NoteSK</dark_purple>]</grey> <red>The plugin is not up to date!</red>"))
                    Main.INSTANCE.server.consoleSender.sendMessage(miniMessages.deserialize(" - Current version: <red>v${pluginVersion}</red>"))
                    Main.INSTANCE.server.consoleSender.sendMessage(miniMessages.deserialize(" - Available update: <green>v${version}</green>"))
                    Main.INSTANCE.server.consoleSender.sendMessage(miniMessages.deserialize(" - Download available at: <dark_purple>https://github.com/byPixelTV/NoteSK/releases</dark_purple>"))
                    UPDATE_VERSION = curVer
                }
            }
        }

        fun getLatestReleaseVersion(consumer: Consumer<String>) {
            val miniMessages = MiniMessage.miniMessage()
            try {
                val url = URI("https://api.github.com/repos/byPixelTV/NoteSK/releases/latest")
                val reader = BufferedReader(InputStreamReader(url.toURL().openStream()))
                val jsonObject = Gson().fromJson(reader, JsonObject::class.java)
                var tagName = jsonObject["tag_name"].asString
                tagName = tagName.removePrefix("v")
                consumer.accept(tagName)
            } catch (e: IOException) {
                Main.INSTANCE.server.consoleSender.sendMessage(miniMessages.deserialize("<grey>[<dark_purple>NoteSK</dark_purple>]</grey> <red>Checking for updates failed! Maybe this stacktrace will help?<br>$e</red>"))
            }
        }
    }

    @Suppress("RedundantSamConstructor")
    fun getUpdateVersion(currentVersion: String): CompletableFuture<Version> {
        val future = CompletableFuture<Version>()
        if (UPDATE_VERSION != null) {
            future.complete(UPDATE_VERSION)
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, Runnable {
                getLatestReleaseVersion(Consumer { version ->
                    val plugVer = Version(currentVersion)
                    val curVer = Version(version)
                    if (curVer <= plugVer) {
                        future.cancel(true)
                    } else {
                        UPDATE_VERSION = curVer
                        future.complete(UPDATE_VERSION)
                    }
                })
            })
        }
        return future
    }
}