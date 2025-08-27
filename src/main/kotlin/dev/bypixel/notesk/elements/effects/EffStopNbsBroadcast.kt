package dev.bypixel.notesk.elements.effects

import ch.njol.skript.Skript
import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.Effect
import ch.njol.skript.lang.Expression
import ch.njol.skript.lang.SkriptParser
import ch.njol.util.Kleenean
import dev.bypixel.notesk.Main
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.jetbrains.annotations.Nullable

@Name("stop broadcast song")
@Description("Stop all songs from being broadcasting to all players on the server.")
@Since("1.0.0")
class EffStopNbsBroadcast : Effect() {
    companion object{
        init {
            Skript.registerEffect(EffStopNbsBroadcast::class.java, "[(skmusic|nbs|notesk)] stop broadcast[ing] (song|music)")
        }
    }

    override fun init(
        expressions: Array<Expression<*>>,
        matchedPattern: Int,
        isDelayed: Kleenean,
        parser: SkriptParser.ParseResult
    ): Boolean {
        return true
    }

    override fun toString(@Nullable e: Event?, b: Boolean): String {
        return "[(skmusic|nbs)] stop broadcast[ing] (song|music)"
    }
    public override fun execute(e: Event?) {
        for (p in Bukkit.getOnlinePlayers()) {
            if (Main.songPlayers.containsKey(p)) {
                Main.songPlayers[p]?.isPlaying = false
                Main.songPlayers[p]?.destroy()
            }
        }
    }
}