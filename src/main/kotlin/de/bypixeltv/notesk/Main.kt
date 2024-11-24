package de.bypixeltv.notesk

import ch.njol.skript.Skript
import ch.njol.skript.SkriptAddon
import com.xxmicloxx.NoteBlockAPI.songplayer.SongPlayer
import de.bypixeltv.notesk.commands.Commands
import de.bypixeltv.notesk.utils.IngameUpdateChecker
import de.bypixeltv.notesk.utils.UpdateChecker
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIBukkitConfig
import net.axay.kspigot.main.KSpigot
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.entity.Player
import java.io.File
import java.io.IOException

class Main : KSpigot() {

    private val miniMessages = MiniMessage.miniMessage()

    private var addon: SkriptAddon? = null

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

    @Suppress("DEPRECATION")
    override fun startup() {
        saveDefaultConfig()
        CommandAPI.onEnable()

        this.instance = this
        this.addon = Skript.registerAddon(this)
        val localAddon = this.addon
        try {
            localAddon?.loadClasses("de.bypixeltv.notesk", "elements")
        } catch (e: IOException) {
            e.printStackTrace()
        }

        server.consoleSender.sendMessage(miniMessages.deserialize("<grey>[<dark_purple>NoteSK</dark_purple>]</grey> <dark_purple>Successfully enabled NoteSK v${this.description.version}!</dark_purple>"))
        Metrics(this, 21632)
        val dir: File = this.dataFolder
        if (!dir.exists()) {
            server.consoleSender.sendMessage(miniMessages.deserialize("<grey>[<dark_purple>NoteSK</dark_purple>]</grey> <color:#43fa00>Creating NoteSK folder...</color>"))
            dir.mkdirs()
        }
        val songsDir = File(this.dataFolder, "songs")
        if (!songsDir.exists()) {
            server.consoleSender.sendMessage(miniMessages.deserialize("<grey>[<dark_purple>NoteSK</dark_purple>]</grey> <color:#43fa00>Creating songs folder...</color>"))
            songsDir.mkdirs()
        }

        IngameUpdateChecker

        val version = description.version
        if (version.contains("-")) {
            server.consoleSender.sendMessage(miniMessages.deserialize("<grey>[<dark_purple>NoteSK</dark_purple>]</grey> <yellow>This is a BETA build, things may not work as expected, please report any bugs on GitHub</yellow>"))
            server.consoleSender.sendMessage(miniMessages.deserialize("<grey>[<dark_purple>NoteSK</dark_purple>]</grey> <yellow>https://github.com/byPixelTV/NoteSK/issues</yellow>"))
        }

        UpdateChecker.checkForUpdate(version)

        Metrics(this, 21632)

        val pluginManager = server.pluginManager
        val noteblockAPIPlugin = pluginManager.getPlugin("NoteBlockAPI")

        if (noteblockAPIPlugin == null) {
            server.consoleSender.sendMessage(miniMessages.deserialize("<grey>[<dark_purple>NoteSK</dark_purple>]</grey> <red>NoteblockAPI is not installed, please install it to use NoteSK</red>"))
            server.pluginManager.disablePlugin(this)
        }
    }

    @Suppress("DEPRECATION")
    override fun shutdown() {
        CommandAPI.onDisable()
    }
}