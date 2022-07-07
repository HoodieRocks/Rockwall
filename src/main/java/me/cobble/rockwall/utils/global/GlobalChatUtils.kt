package me.cobble.rockwall.utils.global

import me.clip.placeholderapi.PlaceholderAPI
import me.cobble.rockwall.utils.Config
import me.cobble.rockwall.utils.Utils
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.entity.Player

object GlobalChatUtils {

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
                Utils.color(
                    PlaceholderAPI.setPlaceholders(
                        player,
                        flattenStringList(section.getStringList("hover"))
                    )
                )
            )
        )
        format.clickEvent = ClickEvent(
            ClickEvent.Action.SUGGEST_COMMAND,
            PlaceholderAPI.setPlaceholders(player, section.getString("on-click")!!)
        )

        return format
    }

    fun flattenStringList(list: List<String>): String {
        val builder: StringBuilder = StringBuilder()
        list.forEach {
            builder.append(it).append('\n')
        }

        return builder.toString()
    }
}