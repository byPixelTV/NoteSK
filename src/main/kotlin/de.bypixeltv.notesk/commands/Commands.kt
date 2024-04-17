package de.bypixeltv.notesk.commands

import ch.njol.skript.Skript
import de.bypixeltv.notesk.Main
import de.bypixeltv.notesk.utils.GetVersion
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import net.kyori.adventure.text.minimessage.MiniMessage
import java.nio.file.Files
import java.nio.file.Paths

class Commands {
    private val miniMessages = MiniMessage.miniMessage()

    val command = commandTree("notesk") {
        withPermission("notesk.admin")
        literalArgument("info") {
            withPermission("notesk.admin.info")
            playerExecutor { player, _ ->
                val addonMessages = Skript.getAddons().mapNotNull { addon ->
                    val name = addon.name
                    if (!name.contains("NoteSK")) {
                        "<grey>-</grey> <aqua>$name</aqua> <yellow>v${addon.plugin.description.version}</yellow>"
                    } else {
                        null
                    }
                }

                val addonsList = if (addonMessages.isNotEmpty()) addonMessages.joinToString("\n") else "<color:#ff0000>No other addons found</color>"
                player.sendMessage(
                    miniMessages.deserialize(
                        "<dark_grey>--- <aqua>NoteSK</aqua> <grey>Info:</grey> ---</dark_grey>\n\n<grey>NoteSK Version: <aqua>${Main.INSTANCE.description.version}</aqua>\nSkript Version: <aqua>${GetVersion().getSkriptVersion()}</aqua>\nServer Version: <aqua>${Main.INSTANCE.server.minecraftVersion}</aqua>\nServer Implementation: <aqua>${Main.INSTANCE.server.version}</aqua>\nAddons:\n$addonsList</grey>"
                    )
                )
            }
        }
        literalArgument("docs") {
            withPermission("notesk.admin.docs")
            playerExecutor { player, _ ->
                player.sendMessage(
                    miniMessages.deserialize(
                        "<dark_grey>[<gradient:aqua:blue:aqua>NoteSK</gradient>]</dark_grey> <grey><aqua>Documentation</aqua> for <aqua>NoteSK:</aqua></grey>\n<grey>-</grey> <click:open_url:'https://skripthub.net/docs/?addon=NoteSK'><aqua>SkriptHub</aqua> <dark_grey>(<aqua>Click me!</aqua>)</dark_grey></click>"
                    )
                )
            }
        }
        literalArgument("version") {
            withPermission("notesk.admin.version")
            playerExecutor { player, _ ->
                val githubVersion = GetVersion().getLatestAddonVersion()?.replace("v", "")?.toDouble()
                if (githubVersion != null) {
                    if (githubVersion > Main.INSTANCE.description.version.replace("v", "").toDouble()) {
                        player.sendMessage(miniMessages.deserialize("<dark_grey>[<gradient:aqua:blue:aqua>NoteSK</gradient>]</dark_grey> <color:#43fa00>There is an update available for NoteSK!</color> <aqua>You're on version <yellow>${Main.INSTANCE.description.version}</yellow> and the latest version is <yellow>$githubVersion</yellow></aqua>!\n\n<color:#43fa00>Download the latest version here:</color> <blue>https://github.com/byPixelTV/NoteSK/releases</blue> <aqua>"))
                    } else {
                        if (githubVersion == Main.INSTANCE.description.version.replace("v", "").toDouble()) {
                            player.sendMessage(miniMessages.deserialize("<dark_grey>[<gradient:aqua:blue:aqua>NoteSK</gradient>]</dark_grey> <color:#43fa00>You're on the latest version of NoteSK!</color> <aqua>Version <yellow>${Main.INSTANCE.description.version}</yellow></aqua>"))
                        } else if (githubVersion < Main.INSTANCE.description.version.replace("v", "").toDouble()) {
                            player.sendMessage(miniMessages.deserialize("<dark_grey>[<gradient:aqua:blue:aqua>NoteSK</gradient>]</dark_grey> <color:#ff0000>You're running a development version of NoteSK! Please note that this version may contain bugs!</color> <aqua>Version <color:#ff0000>${Main.INSTANCE.description.version}</color> > <color:#43fa00>${GetVersion().getLatestAddonVersion()}</color></aqua>"))
                        }
                    }
                } else {
                    player.sendMessage(miniMessages.deserialize("<dark_grey>[<gradient:aqua:blue:aqua>NoteSK</gradient>]</dark_grey> <color:#ff0000>Unable to fetch the latest version from Github!</color> <aqua>Are you rate limited?</aqua>"))
                }
            }
        }
        literalArgument("reload") {
            withPermission("notesk.admin.reload")
            playerExecutor { player, _ ->
                Main.INSTANCE.reloadConfig()
                val path = Paths.get("/plugins/NoteSK/config.yml")
                if (Files.exists(path)) {
                    Main.INSTANCE.saveConfig()
                } else {
                    Main.INSTANCE.saveDefaultConfig()
                }
                player.sendMessage(miniMessages.deserialize("<dark_grey>[<gradient:aqua:blue:aqua>NoteSK</gradient>]</dark_grey> <color:#43fa00>Successfully reloaded the config!</color>"))
            }
        }
    }
}