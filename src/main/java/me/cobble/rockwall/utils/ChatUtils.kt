package me.cobble.rockwall.utils

import dev.dejvokep.boostedyaml.block.implementation.Section
import me.cobble.rockwall.config.Config
import me.cobble.rockwall.config.FormatsConfig
import me.cobble.rockwall.config.models.Features
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object ChatUtils {

    fun isGlobalChatEnabled(): Boolean = Config.getBool("global-chat.enabled")


    /**
     * Gets a player's highest format by their permission
     */
    fun getFormatByPermission(p: Player): String {
        val keys = getFormats().keys
        for (key in keys) {
            if (p.hasPermission("rockwall.format.$key") && key != "default") {
                return key as String
            }
        }
        return "default"
    }

    fun processMessageFeatures(msg: String, player: Player): String {
        val transformedString: String = processMentions(msg, player)
        return processEmojis(transformedString)
    }

    private fun processMentions(msg: String, player: Player): String {
        if (!Features.areMentionsEnabled()) return msg
        val mentionExpression = Regex("@[A-z]+")
        val retrievedSound = Features.getMentionSound()
        if (mentionExpression.containsMatchIn(msg)) {
            mentionExpression.findAll(msg).forEach {
                if ((it.value == "@everyone" || it.value == "@here") && player.hasPermission("rockwall.massmention")) {
                    if (Features.canPlayMentionSound()) {
                        for (p in Bukkit.getServer().onlinePlayers) {
                            p.playSound(p.location, retrievedSound, 0.7f, 2f)
                        }
                    }
                } else {
                    val mentioned = Bukkit.getPlayer(it.value.drop(1))
                    if (Features.canPlayMentionSound()) {
                        mentioned?.playSound(mentioned.location, retrievedSound, 0.7f, 2f)
                    }
                }

                return msg.replace(it.value, Features.getMentionsFormat().replace("%format%", it.value, true), true)
            }
        }
        return msg
    }

    private fun processEmojis(msg: String): String {
        if (!Features.areEmojisEnabled()) return msg
        val emojiExpression = Regex(":[A-z]+:")
        val emojis = Features.getAllEmojis()
        if (emojiExpression.containsMatchIn(msg)) {
            emojiExpression.findAll(msg).forEach {
                for (emoji in emojis) {
                    if (emoji.lowercase() == it.value.lowercase().replace(":", "")) {
                        return msg.replace(it.value, Features.getEmoji(emoji).orElse(""))
                    }
                }
            }
        }
        return msg
    }

    private fun getFormats(): Section {
        return FormatsConfig.getSection("global-chat-formats")!!
    }
}
