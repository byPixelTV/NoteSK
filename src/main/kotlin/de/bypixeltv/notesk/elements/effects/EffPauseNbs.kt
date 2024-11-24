package de.bypixeltv.notesk.elements.effects

import ch.njol.skript.Skript
import ch.njol.skript.lang.Effect
import ch.njol.skript.lang.Expression
import ch.njol.skript.lang.SkriptParser
import ch.njol.util.Kleenean
import com.xxmicloxx.NoteBlockAPI.NoteBlockAPI
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.jetbrains.annotations.Nullable

class EffPauseNbs : Effect() {

    companion object {
        init {
            Skript.registerEffect(EffPauseNbs::class.java, "[(skmusic|nbs|notesk)] pause (song|music) of %player%")
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
        return "[(skmusic|nbs|notesk)] pause (song|music) of %player%"
    }

    public override fun execute(e: Event?) {
        val p: Player = player?.getSingle(e) ?: return
        for (songPlayer in NoteBlockAPI.getSongPlayersByPlayer(p)) {
            songPlayer.isPlaying = false
        }
    }
}
