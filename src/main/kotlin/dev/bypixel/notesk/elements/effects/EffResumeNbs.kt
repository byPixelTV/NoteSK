package dev.bypixel.notesk.elements.effects

import ch.njol.skript.Skript
import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.Effect
import ch.njol.skript.lang.Expression
import ch.njol.skript.lang.SkriptParser
import ch.njol.util.Kleenean
import com.xxmicloxx.NoteBlockAPI.NoteBlockAPI
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.jetbrains.annotations.Nullable

@Name("resume song")
@Description("Resume the currently paused note block song for a player.")
@Since("1.0.0")
class EffResumeNbs : Effect() {
    companion object{
        init {
            Skript.registerEffect(EffResumeNbs::class.java, "[(skmusic|nbs|notesk)] resume (song|music) of %player%")
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
        return "[(skmusic|nbs|notesk)] resume (song|music) of %player%"
    }

    public override fun execute(e: Event?) {
        val p: Player = player?.getSingle(e) ?: return
        for (songPlayer in NoteBlockAPI.getSongPlayersByPlayer(p)) {
            songPlayer.isPlaying = true
        }
    }
}