package me.cobble.rockwall.config.models

import dev.dejvokep.boostedyaml.block.implementation.Section
import me.cobble.rockwall.config.Config
import org.bukkit.Sound
import java.util.*

object Features {
    private const val FEATURES_PATH = "global-chat.features"
    private const val MENTION_PATH = "$FEATURES_PATH.mentions"
    private const val EMOJIS_FEATURES_PATH = "$FEATURES_PATH.emojis"
    private const val EMOJIS_PATH = "emojis"

    fun areEmojisEnabled(): Boolean = Config.getBool("$EMOJIS_FEATURES_PATH.enabled")

    fun areMentionsEnabled(): Boolean = Config.getBool("$MENTION_PATH.enabled")

    fun getMentionSound(): Sound =
        Sound.valueOf(Config.getString("$MENTION_PATH.sound").orElse("block_note_block_pling").uppercase())

    fun canPlayMentionSound(): Boolean = Config.getBool("$MENTION_PATH.play-sound")

    fun getMentionsFormat(): String = Config.getString("$MENTION_PATH.format").orElse("&9%format%&f")

    fun getChatRoomSection(): Section = Config.getSection("chat-rooms")!!

    /**
     * Gets all emojis from the list
     */
    fun getAllEmojis(): Set<String> {
        return Config.getSection(EMOJIS_PATH)!!.getRoutesAsStrings(false)
    }

    /**
     * Gets a single emoji from the config
     */
    fun getEmoji(path: String): Optional<String> {
        return Config.getString("$EMOJIS_PATH.$path")
    }

    fun areChatroomsEnabled(): Boolean {
        return Config.getBool("$FEATURES_PATH.chat-rooms.enabled")
    }
}