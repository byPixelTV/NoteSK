package de.bypixeltv.notesk.elements.effects

import ch.njol.skript.Skript
import ch.njol.skript.lang.Effect
import ch.njol.skript.lang.Expression
import ch.njol.skript.lang.SkriptParser
import ch.njol.util.Kleenean
import de.bypixeltv.notesk.Main
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.jetbrains.annotations.Nullable


class EffSetVolume : Effect() {

    companion object{
        init {
            Skript.registerEffect(EffSetVolume::class.java, "[(skmusic|nbs|notesk)] set volume of [player] %player% to %integer%")
        }
    }

    private var vol: Expression<Int>? = null
    private var player: Expression<Player>? = null

    @Suppress("UNCHECKED_CAST")
    override fun init(
        expressions: Array<Expression<*>>,
        matchedPattern: Int,
        isDelayed: Kleenean,
        parser: SkriptParser.ParseResult
    ): Boolean {
        this.player = expressions[0] as Expression<Player>
        this.vol = expressions[1] as Expression<Int>
        return true
    }

    override fun toString(@Nullable e: Event?, b: Boolean): String {
        return "[(skmusic|nbs|notesk)] set volume of [player] %player% to %integer%"
    }

    public override fun execute(e: Event?) {
        val p = player!!.getSingle(e)
        val volume = vol!!.getSingle(e)!!.toByte()
        if (Main.songPlayers.containsKey(p)) {
            Main.songPlayers[p]?.volume = volume
        }
    }
}