package me.cobble.rockwall.utils.global

import me.clip.placeholderapi.PlaceholderAPI
import me.cobble.rockwall.rockwall.Config
import me.cobble.rockwall.utils.Utils
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.entity.Player

object GlobalChatUtils {

    /**
     * Creates string formats from config entries
     * @return component with click, hover event and display text
     */
    fun formatMaker(player: Player, formatName: String, type: FormatType): TextComponent? {
        if (formatName.isBlank()) return null
        val configSection = Config.get()!!.getConfigurationSection("global-chat.formats.$formatName") ?: return null
        val section = configSection.getConfigurationSection(type.getType())
        val format = TextComponent(
            *TextComponent.fromLegacyText(
                Utils.color(
                    PlaceholderAPI.setPlaceholders(
                        player,
                        section!!.getString("display")!!
                    )
                )
            )
        )
        format.hoverEvent = HoverEvent(
            HoverEvent.Action.SHOW_TEXT, Text(
                TextComponent.fromLegacyText(
                    Utils.color(
                        Utils.setPlaceholders(player, Utils.flattenStringList(section.getStringList("hover")))
                    )
                )
            )
        )
        format.clickEvent = ClickEvent(
            ClickEvent.Action.SUGGEST_COMMAND,
            Utils.setPlaceholders(player, section.getString("on-click")!!)
        )

        return format
    }

    /**
     * Check if global chat is enabled in config
     * @return if global chat is enabled
     */
    fun isGlobalChatEnabled(): Boolean {
        return Config.getBool("global-chat.enabled")
    }

    /**
     * Gets the highest permission for a player
     * @return format name
     */
    fun getFormatPermission(p: Player): String {
        val keys = Config.get()!!.getConfigurationSection("global-chat.formats")!!.getKeys(false)
        for (key in keys) {
            if (p.hasPermission("rockwall.format.$key") && key != "default") {
                return key
            }
        }
        return "default"
    }
}