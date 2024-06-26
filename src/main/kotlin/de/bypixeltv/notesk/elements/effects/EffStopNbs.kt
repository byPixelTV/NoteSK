package de.bypixeltv.notesk.elements.effects

import ch.njol.skript.Skript
import ch.njol.skript.lang.Effect
import ch.njol.skript.lang.Expression
import ch.njol.skript.lang.SkriptParser
import ch.njol.util.Kleenean
import de.bypixeltv.notesk.Main
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.jetbrains.annotations.Nullable


class EffStopNbs : Effect() {

    private val miniMessages = MiniMessage.miniMessage()

    companion object{
        init {
            Skript.registerEffect(EffStopNbs::class.java, "[(skmusic|nbs|notesk)] stop play[ing] (song|music) to %player%")
        }
    }

    private var player: Expression<Player>? = null

    @Suppress("UNCHECKED_CAST")
    override fun init(
        expressions: Array<Expression<*>>,
        matchedPattern: Int,
        isDelayed: Kleenean,
        parser: SkriptParser.ParseResult
    ): Boolean {
        this.player = expressions[0] as Expression<Player>
        return true
    }

    override fun toString(@Nullable e: Event?, b: Boolean): String {
        return "[(skmusic|nbs|notesk)] stop play[ing] (song|music) to %player%"
    }

    public override fun execute(e: Event?) {
        val p: Player = player?.getSingle(e) ?: return
        Main.songPlayers[p]?.isPlaying = false
        Main.songPlayers[p]?.destroy()
    }
}