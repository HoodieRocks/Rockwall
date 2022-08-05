package me.cobble.rockwall.utils.chat

import me.cobble.rockwall.config.Config
import me.cobble.rockwall.utils.Utils
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.event.ClickEvent
import org.bukkit.entity.Player

object ChatUtils {

    /**
     * Creates string formats from config entries
     * @return component with click, hover event and display text
     */
    fun makeFormat(player: Player, formatName: String, type: FormatType): TextComponent {
        if (formatName.isBlank()) return Component.empty()
        val configSection = Config.getSection("global-chat.formats.$formatName") ?: return Component.empty()
        val section = configSection.getConfigurationSection(type.getType())

        return Utils.colorAndComponent(Utils.setPlaceholders(player, section!!.getString("display")!!)).hoverEvent(
            Utils.colorAndComponent(
                Utils.setPlaceholders(
                    player,
                    Utils.flattenList(section.getStringList("hover"))
                )
            ).asHoverEvent()
        ).clickEvent(ClickEvent.suggestCommand(Utils.setPlaceholders(player, section.getString("on-click")!!)))
    }

    fun isGlobalChatEnabled(): Boolean {
        return Config.getBool("global-chat.enabled")
    }

    fun getFormatPermission(p: Player): String {
        val keys = Config.getSection("global-chat.formats")!!.getKeys(false)
        for (key in keys) {
            if (p.hasPermission("rockwall.format.$key") && key != "default") {
                return key
            }
        }
        return "default"
    }
}