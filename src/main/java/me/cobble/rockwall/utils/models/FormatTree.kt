package me.cobble.rockwall.utils.models

import dev.dejvokep.boostedyaml.block.implementation.Section
import me.cobble.rockwall.config.FormatsConfig
import me.cobble.rockwall.utils.ColorUtils
import me.cobble.rockwall.utils.FormatUtils
import me.cobble.rockwall.utils.parties.models.Party
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.entity.Player
import java.util.*

/**
 * Represents a format tree
 * @param path - Path to tree (must be the root)
 *
 * @since 1.3.0
 */
class FormatTree(private val path: String) {
    private val configTree = FormatsConfig.getSection(path)!!

    fun getGroupPrefix(key: String): AbstractFormatSection {
        return AbstractFormatSection(key, "prefix", configTree)
    }

    fun getGroupPlayerName(key: String): AbstractFormatSection {
        return AbstractFormatSection(key, "name", configTree)
    }

    fun getGroupPrefixSeparator(key: String): AbstractFormatSection {
        return AbstractFormatSection(key, "prefix-separator", configTree)
    }

    fun getGroupNameSeparator(key: String): AbstractFormatSection {
        return AbstractFormatSection(key, "name-separator", configTree)
    }

    fun getGroupSuffix(key: String): AbstractFormatSection {
        return AbstractFormatSection(key, "suffix", configTree)
    }

    fun getGroupSuffixSeparator(key: String): AbstractFormatSection {
        return AbstractFormatSection(key, "suffix-separator", configTree)
    }

    fun getGroupChatColor(key: String): Optional<String> {
        return configTree.getOptionalString("$key.chat-color")
    }


    fun asRockwallFormat(player: Player, formatSection: AbstractFormatSection): TextComponent {
        val format =
            ColorUtils.colorizeComponents(ColorUtils.setPlaceholders(player, formatSection.getDisplay().orElse("")))

        format.font = formatSection.getFont().orElse("minecraft:default")
        format.hoverEvent = HoverEvent(
            HoverEvent.Action.SHOW_TEXT,
            Text(FormatUtils.formatStringList(formatSection.getHover(), player).create())
        )
        format.clickEvent = ClickEvent(
            ClickEvent.Action.SUGGEST_COMMAND,
            ColorUtils.setPlaceholders(player, formatSection.getOnClick().orElse(""))
        )

        return format
    }

    fun asRockwallFormat(player: Player, formatSection: AbstractFormatSection, party: Party): TextComponent {
        val format =
            ColorUtils.colorizeComponents(ColorUtils.setPlaceholders(player, formatSection.getDisplay().orElse("")))

        format.font = formatSection.getFont().orElse("minecraft:default")
        format.hoverEvent = HoverEvent(
            HoverEvent.Action.SHOW_TEXT,
            Text(
                FormatUtils.formatStringList(
                    FormatUtils.customPlaceholders(
                        formatSection.getHover(),
                        party
                    ), player
                ).create()
            )
        )
        format.clickEvent = ClickEvent(
            ClickEvent.Action.SUGGEST_COMMAND,
            ColorUtils.setPlaceholders(
                player,
                FormatUtils.customPlaceholders(formatSection.getOnClick().orElse(""), party)
            )
        )

        return format
    }

    class AbstractFormatSection(nameKey: String, valueKey: String, private val configTree: Section) {
        private val path = "$nameKey.$valueKey"
        fun getDisplay(): Optional<String> {
            return configTree.getOptionalString("$path.display")
        }

        fun getHover(): MutableList<String> {
            return configTree.getStringList("$path.hover")
        }

        fun getOnClick(): Optional<String> {
            return configTree.getOptionalString("$path.on-click")
        }

        fun getFont(): Optional<String> {
            return configTree.getOptionalString("$path.font")
        }
    }
}