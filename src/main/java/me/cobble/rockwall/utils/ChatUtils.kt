package me.cobble.rockwall.utils

import dev.dejvokep.boostedyaml.block.implementation.Section
import me.cobble.rockwall.config.Config
import me.cobble.rockwall.config.models.ChatFormatType
import me.cobble.rockwall.config.models.Features
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object ChatUtils {

    private var formats: Section? = null

    /**
     * Creates string formats from config entries
     * @return component with click, hover event and display text
     */
    fun makeFormat(player: Player, formatName: String, type: ChatFormatType): TextComponent? {
        if (formatName.isBlank()) return null
        val configSection = Config.getSection("global-chat.formats.$formatName") ?: return null
        val section = configSection.getSection(type.getType())
        val format =
            ColorUtils.colorToTextComponent(ColorUtils.setPlaceholders(player, section!!.getString("display")!!))

        format.font = section.getOptionalString("font").orElse("minecraft:default")
        format.hoverEvent = HoverEvent(
            HoverEvent.Action.SHOW_TEXT,
            Text(FormatUtils.formatStringList(section.getStringList("hover"), player).create())
        )
        format.clickEvent = ClickEvent(
            ClickEvent.Action.SUGGEST_COMMAND,
            ColorUtils.setPlaceholders(player, section.getOptionalString("on-click").orElse(""))
        )

        return format
    }

    fun isGlobalChatEnabled(): Boolean = Config.getBool("global-chat.enabled")


    fun getFormatByPermission(p: Player): String {
        val keys = getFormats()!!.keys
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
        if (!Features.mentionsEnabled()) return msg
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
        if (!Features.emojisEnabled()) return msg
        val emojiExpression = Regex(":[A-z]+:")
        val emojis = Features.getAllEmojis()
        if (emojiExpression.containsMatchIn(msg)) {
            emojiExpression.findAll(msg).forEach {
                for (emoji in emojis) {
                    if ((emoji as String).lowercase() == it.value.lowercase().replace(":", "")) {
                        return msg.replace(it.value, Features.getEmoji(emoji).orElse(""))
                    }
                }
            }
        }
        return msg
    }

    private fun getFormats(): Section? {
        if (formats == null) {
            formats = Config.getSection("global-chat.formats")!!
        }
        return formats
    }
}
