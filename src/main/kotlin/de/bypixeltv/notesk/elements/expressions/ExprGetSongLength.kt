package de.bypixeltv.notesk.elements.expressions

import ch.njol.skript.Skript
import ch.njol.skript.lang.Expression
import ch.njol.skript.lang.ExpressionType
import ch.njol.skript.lang.SkriptParser
import ch.njol.skript.lang.util.SimpleExpression
import ch.njol.util.Kleenean
import com.xxmicloxx.NoteBlockAPI.model.Song
import de.bypixeltv.notesk.Main
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.jetbrains.annotations.Nullable


class ExprGetSongLength : SimpleExpression<Number>() {

    companion object{
        init {
            Skript.registerExpression(
                ExprGetSongLength::class.java, Number::class.java,
                ExpressionType.SIMPLE, "[(skmusic|nbs|notesk)] %player%['s] (song|music) (length|duration)")
        }
    }

    private var player: Expression<Player>? = null

    override fun isSingle(): Boolean {
        return true
    }

    @Suppress("UNCHECKED_CAST")
    override fun init(
        exprs: Array<Expression<*>>,
        matchedPattern: Int,
        isDelayed: Kleenean?,
        parseResult: SkriptParser.ParseResult?
    ): Boolean {
        this.player = exprs[0] as Expression<Player>?
        return true
    }

    @Nullable
    override fun get(e: Event?): Array<out Number?> {
        val p = player!!.getSingle(e)
        var length = 0
        if (Main.songPlayers.containsKey(p)) {
            val song: Song? = Main.songPlayers[p]?.song
            length = (song?.length ?: 0).toInt()
        }
        return arrayOf(length)
    }

    override fun getReturnType(): Class<out Number> {
        return Number::class.java
    }

    override fun toString(e: Event?, debug: Boolean): String {
        return "[(skmusic|nbs|notesk)] %player%['s] (song|music) (length|duration)"
    }

}