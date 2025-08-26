package dev.bypixel.notesk.elements.conditions

import ch.njol.skript.Skript
import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.Condition
import ch.njol.skript.lang.Expression
import ch.njol.skript.lang.SkriptParser
import ch.njol.util.Kleenean
import dev.bypixel.notesk.Main
import org.bukkit.event.Event
import java.io.File

@Name("song exists")
@Description("Checks if a song exists in the plugin folder.")
@Since("1.0.0")
class CondNoteblockAPISongExists : Condition() {
    companion object{
        init {
            Skript.registerCondition(CondNoteblockAPISongExists::class.java, "[(skmusic|nbs|notesk)] (song|music) %string% exists")
        }
    }

    private var song: Expression<String>? = null

    @Suppress("UNCHECKED_CAST")
    override fun init(
        expressions: Array<Expression<*>>,
        matchedPattern: Int,
        isDelayed: Kleenean?,
        parseResult: SkriptParser.ParseResult?
    ): Boolean {
        this.song = expressions[0] as Expression<String>?
        isNegated = parseResult?.mark == 1
        return true
    }
    override fun check(e: Event?): Boolean {
        var song = song?.getSingle(e) ?: return isNegated
        if (!song.contains(".nbs")) {
            song = "$song.nbs"
        }
        val f: File = File(Main.INSTANCE.dataFolder,song)
        if (f.exists()) {
            return !isNegated
        }
        return isNegated
    }

    override fun toString(e: Event?, debug: Boolean): String {
        return "[(song|music)] (song|music) %string% exists"
    }

}