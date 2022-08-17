package me.cobble.rockwall.utils

import me.cobble.rockwall.config.Config
import me.cobble.rockwall.config.Emojis
import me.cobble.rockwall.config.models.ChatFormatType
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.entity.Player

object ChatUtils {

    /**
     * Creates string formats from config entries
     * @return component with click, hover event and display text
     */
    fun makeFormat(player: Player, formatName: String, type: ChatFormatType): TextComponent? {
        if (formatName.isBlank()) return null
        val configSection = Config.getSection("global-chat.formats.$formatName") ?: return null
        val section = configSection.getSection(type.getType())
        val hover = ComponentBuilder().append(
            TextComponent(
                Formats.color(
                    Formats.setPlaceholders(player, Formats.flattenList(section.getStringList("hover")))
                )
            )
        )
        val format = TextComponent(
            *TextComponent.fromLegacyText(
                Formats.color(
                    Formats.setPlaceholders(player, section!!.getString("display")!!)
                )
            )
        )

        hover.retain(ComponentBuilder.FormatRetention.NONE)

        format.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text(hover.create()))
        format.clickEvent = ClickEvent(
            ClickEvent.Action.SUGGEST_COMMAND,
            Formats.setPlaceholders(player, section.getString("on-click")!!)
        )

        return format
    }

    fun isGlobalChatEnabled(): Boolean {
        return Config.getBool("global-chat.enabled")
    }


    fun getFormatByPermission(p: Player): String {
        val keys = Config.getSection("global-chat.formats")!!.keys
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
        if (!Config.getBool("global-chat.features.mentions.enabled")) return msg
        val mentionExpression = Regex("@[A-z]+")
        val retrievedSound = Sound.valueOf(
            Config.getString("global-chat.features.mentions.sound")
                .orElse("block_note_block_pling")
                .uppercase()
        )
        if (mentionExpression.containsMatchIn(msg)) {
            mentionExpression.findAll(msg).forEach {
                if ((it.value == "@everyone" || it.value == "@here") && player.hasPermission("rockwall.massmention")) {
                    if (Config.getBool("global-chat.features.mentions.play-sound")) {
                        for (p in Bukkit.getServer().onlinePlayers) {
                            p.playSound(p.location, retrievedSound, 0.3f, 1f)
                        }
                    }
                } else {
                    val mentioned = Bukkit.getPlayer(it.value.drop(1))
                    if (Config.getBool("global-chat.features.mentions.play-sound")) {
                        mentioned?.playSound(mentioned.location, retrievedSound, 0.3f, 1f)
                    }
                }

                return msg.replace(
                    it.value,
                    Config.getString("global-chat.features.mentions.format")
                        .orElse("block_note_block_pling")
                        .replace("%format%", it.value, true),
                    true
                )
            }
        }
        return msg
    }

    private fun processEmojis(msg: String): String {
        if (!Config.getBool("global-chat.features.emojis.enabled")) return msg
        val emojiExpression = Regex(":[A-z]+:")
        val emojis = Emojis.getAllEmojis()
        if (emojiExpression.containsMatchIn(msg)) {
            emojiExpression.findAll(msg).forEach {
                for (emoji in emojis) {
                    if ((emoji as String).lowercase() == it.value.lowercase().replace(":", "")) {
                        return msg.replace(it.value, Emojis.getEmoji(emoji))
                    }
                }
            }
        }
        return msg
    }
}
