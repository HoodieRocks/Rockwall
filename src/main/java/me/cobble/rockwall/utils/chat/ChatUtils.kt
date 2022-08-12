package me.cobble.rockwall.utils.chat

import me.cobble.rockwall.config.Config
import me.cobble.rockwall.utils.Formats
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.entity.Player

object ChatUtils {

    /**
     * Creates string formats from config entries
     * @return component with click, hover event and display text
     */
    fun makeFormat(player: Player, formatName: String, type: FormatType): TextComponent? {
        if (formatName.isBlank()) return null
        val configSection = Config.getSection("global-chat.formats.$formatName") ?: return null
        val section = configSection.getSection(type.getType())

        val format = TextComponent(
            *TextComponent.fromLegacyText(
                Formats.color(
                    Formats.setPlaceholders(player, section!!.getString("display")!!)
                )
            )
        )

        format.hoverEvent = HoverEvent(
            HoverEvent.Action.SHOW_TEXT,
            Text(
                TextComponent.fromLegacyText(
                    Formats.color(
                        Formats.setPlaceholders(player, Formats.flattenList(section.getStringList("hover")))
                    )
                )
            )
        )
        format.clickEvent = ClickEvent(
            ClickEvent.Action.SUGGEST_COMMAND,
            Formats.setPlaceholders(player, section.getString("on-click")!!)
        )

        return format
    }

    fun isGlobalChatEnabled(): Boolean {
        return Config.getBool("global-chat.enabled")
    }

    fun getFormatPermission(p: Player): String {
        val keys = Config.getSection("global-chat.formats")!!.keys
        for (key in keys) {
            if (p.hasPermission("rockwall.format.$key") && key != "default") {
                return key as String
            }
        }
        return "default"
    }
}
