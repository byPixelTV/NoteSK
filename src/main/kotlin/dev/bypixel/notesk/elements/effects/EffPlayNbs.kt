package dev.bypixel.notesk.elements.effects

import ch.njol.skript.Skript
import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.Effect
import ch.njol.skript.lang.Expression
import ch.njol.skript.lang.SkriptParser
import ch.njol.util.Kleenean
import com.xxmicloxx.NoteBlockAPI.model.Song
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer
import com.xxmicloxx.NoteBlockAPI.songplayer.SongPlayer
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder
import dev.bypixel.notesk.Main
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.jetbrains.annotations.Nullable
import java.io.File

@Name("play song")
@Description("Play a note block song to a specific player.")
@Since("1.0.0")
class EffPlayNbs : Effect() {

    private val miniMessages = MiniMessage.miniMessage()

    companion object{
        init {
            Skript.registerEffect(EffPlayNbs::class.java, "[(skmusic|nbs|notesk)] play (song|music) %string% to %player% [at tick %number%]")
        }
    }

    private var song: Expression<String>? = null
    private var player: Expression<Player>? = null
    private var tick: Expression<Number>? = null

    @Suppress("UNCHECKED_CAST")
    override fun init(
        expressions: Array<Expression<*>>,
        matchedPattern: Int,
        isDelayed: Kleenean,
        parser: SkriptParser.ParseResult
    ): Boolean {
        this.song = expressions[0] as Expression<String>
        this.player = expressions[1] as Expression<Player>
        this.tick = expressions[2] as Expression<Number>
        return true
    }

    override fun toString(@Nullable e: Event?, b: Boolean): String {
        return "[(skmusic|nbs|notesk)] play (song|music) %string% to %player%"
    }
    public override fun execute(e: Event?) {
        val p: Player = player?.getSingle(e) ?: return
        var fileName: String = song?.getSingle(e).toString()
        if (!fileName.endsWith(".nbs")) {
            fileName = "$fileName.nbs"
        }
        val music = File(Main.INSTANCE.songsDir, fileName)
        if (!music.exists()) {
            Main.INSTANCE.server.consoleSender.sendMessage(miniMessages.deserialize("<grey>[<aqua>NoteSK</aqua>]</grey> <color:#ff0000>Error while trying to load the song <yellow>$fileName</yellow>! Does the file exist?"))
        } else {
            val s: Song = NBSDecoder.parse(music)
            val sp: SongPlayer = RadioSongPlayer(s)
            if (Main.songPlayers.containsKey(p)) {
                Main.songPlayers[p]?.destroy()
                Main.songPlayers.replace(p, sp)
            } else {
                Main.songPlayers[p] = sp
            }

            if (tick != null) {
                val t = tick?.getSingle(e)?.toShort() ?: 0
                sp.tick = t
            }

            sp.addPlayer(p)
            sp.autoDestroy = true
            sp.isPlaying = true
        }
    }
}