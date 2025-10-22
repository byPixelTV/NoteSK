package dev.bypixel.notesk

import ch.njol.skript.Skript
import ch.njol.skript.SkriptAddon
import com.xxmicloxx.NoteBlockAPI.songplayer.SongPlayer
import dev.bypixel.notesk.commands.Commands
import dev.bypixel.notesk.utils.IngameUpdateChecker
import dev.bypixel.notesk.utils.UpdateChecker
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIBukkitConfig
import net.axay.kspigot.main.KSpigot
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

class Main : KSpigot() {

    private val miniMessages = MiniMessage.miniMessage()
    private var addon: SkriptAddon? = null
    lateinit var songsDir: File
        private set

    var instance: Main? = null

    companion object {
        lateinit var INSTANCE: Main
            private set
        var songPlayers = HashMap<Player, SongPlayer>()
    }

    override fun load() {
        CommandAPI.onLoad(CommandAPIBukkitConfig(this).silentLogs(true).verboseOutput(true))
        INSTANCE = this
        Commands()
    }

    override fun startup() {
        saveDefaultConfig()
        mergeMissingConfigKeys()
        reloadConfig()
        initSongsDir()

        CommandAPI.onEnable()

        this.instance = this
        this.addon = Skript.registerAddon(this)
        try {
            addon?.loadClasses("dev.bypixel.notesk", "elements")
        } catch (e: IOException) {
            e.printStackTrace()
        }

        server.consoleSender.sendMessage(miniMessages.deserialize("<grey>[<dark_purple>NoteSK</dark_purple>]</grey> <dark_purple>Successfully enabled NoteSK v${pluginMeta.version}!</dark_purple>"))
        Metrics(this, 21632)

        if (!dataFolder.exists()) {
            dataFolder.mkdirs()
        }
        if (!songsDir.exists()) {
            songsDir.mkdirs()
            server.consoleSender.sendMessage(miniMessages.deserialize("<grey>[<dark_purple>NoteSK</dark_purple>]</grey> <color:#43fa00>Created songs folder at: ${songsDir.path}</color>"))
        }

        IngameUpdateChecker

        val version = pluginMeta.version
        if (version.contains("-")) {
            server.consoleSender.sendMessage(miniMessages.deserialize("<grey>[<dark_purple>NoteSK</dark_purple>]</grey> <yellow>This is a BETA build, things may not work as expected, please report any bugs on GitHub</yellow>"))
            server.consoleSender.sendMessage(miniMessages.deserialize("<grey>[<dark_purple>NoteSK</dark_purple>]</grey> <yellow>https://github.com/byPixelTV/NoteSK/issues</yellow>"))
        }

        UpdateChecker.checkForUpdate(version)
        Metrics(this, 21632)

        val noteblockAPIPlugin = server.pluginManager.getPlugin("NoteBlockAPI")
        if (noteblockAPIPlugin == null) {
            server.consoleSender.sendMessage(miniMessages.deserialize("<grey>[<dark_purple>NoteSK</dark_purple>]</grey> <red>NoteblockAPI is not installed, please install it to use NoteSK</red>"))
            server.pluginManager.disablePlugin(this)
        }
    }

    @Suppress("DEPRECATION")
    override fun shutdown() {
        CommandAPI.onDisable()
    }

    private fun initSongsDir() {
        val raw = config.getString("songs-dir")?.trim()
        val defaultDir = File(dataFolder, "songs")
        songsDir = when {
            raw.isNullOrEmpty() -> {
                logSongsDirReason("songs dir key missing or empty -> Default")
                defaultDir
            }
            raw.equals("default", true) -> {
                logSongsDirReason("Value 'default'")
                defaultDir
            }
            else -> {
                val candidate = File(raw)
                val resolved = if (candidate.isAbsolute) candidate else File(server.worldContainer, raw)
                logSongsDirReason("Config value '$raw' -> using '${resolved.path}' (abs=${resolved.isAbsolute})")
                resolved
            }
        }
    }

    private fun logSongsDirReason(msg: String) {
        server.consoleSender.sendMessage(
            miniMessages.deserialize("<grey>[<dark_purple>NoteSK</dark_purple>]</grey> <grey>songs-dir: $msg</grey>")
        )
    }

    private fun mergeMissingConfigKeys() {
        val configFile = File(dataFolder, "config.yml")
        if (!configFile.exists()) {
            saveDefaultConfig()
        }
        val jarStream = getResource("config.yml") ?: return
        val defaults = YamlConfiguration.loadConfiguration(InputStreamReader(jarStream, StandardCharsets.UTF_8))
        jarStream.close()
        val fileConfig = YamlConfiguration.loadConfiguration(configFile)

        var changed = false
        for (path in defaults.getKeys(true)) {
            if (defaults.isConfigurationSection(path)) continue
            if (!fileConfig.contains(path)) {
                fileConfig.set(path, defaults.get(path))
                changed = true
            }
        }
        if (changed) {
            fileConfig.save(configFile)
            server.consoleSender.sendMessage(
                miniMessages.deserialize("<grey>[<dark_purple>NoteSK</dark_purple>]</grey> <green>Missing config keys were added.</green>")
            )
        }
    }
}