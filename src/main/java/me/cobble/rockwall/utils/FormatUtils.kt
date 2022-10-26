package me.cobble.rockwall.utils

import me.cobble.rockwall.config.Config
import me.cobble.rockwall.utils.models.RockwallBaseCommand
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.entity.Player

object FormatUtils {

    /**
     * Formats sub commands as a file structure display
     */
    fun formatAsFileStructure(list: List<RockwallBaseCommand>, label: String): Array<BaseComponent> {
        val components = arrayListOf<BaseComponent>()
        val sortedList = list.sortedBy { it.name }

        for (i in sortedList.indices) {
            val sc: RockwallBaseCommand = sortedList[i]
            val format = "&e${sc.syntax.replace("[label]", label)} &7- ${sc.descriptor}"
            when (i) {
                0 -> components.add(addEvents(formatCmd("┌ ", format), sc.descriptor, sc.syntax))
                list.size - 1 -> components.add(addEvents(formatCmd("└ ", format), sc.descriptor, sc.syntax))
                else -> components.add(addEvents(formatCmd("├ ", format), sc.descriptor, sc.syntax))
            }
        }
        return components.toTypedArray()
    }

    /**
     * Adds click and hover events
     */
    private fun addEvents(text: String, hoverText: String?, command: String?): BaseComponent {
        val component = ColorUtils.colorToTextComponent(text)
        component.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text(hoverText))
        component.clickEvent = ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command)
        return component
    }

    private fun formatCmd(prefix: String, cmd: String): String = ColorUtils.color("&e$prefix&7$cmd\n")

    /**
     * Checks if string is non-alphanumeric
     * @return if string is alphanumeric
     */
    fun String.containsSpecialCharacters(): Boolean = this.contains(Regex("[^A-Za-z0-9]"))

    fun randomString(int: Int): String {
        val values = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val stringBuilder = StringBuilder()
        for (i in 0 until int) {
            stringBuilder.append(values.random())
        }
        return stringBuilder.toString()
    }

    /**
     * Flattens a string list to a single string
     * @return string list as string
     */
    fun formatStringList(list: List<String>, player: Player): ComponentBuilder {
        val builder = ComponentBuilder()
        list.forEach {
            builder.append(ColorUtils.colorToTextComponent(ColorUtils.setPlaceholders(player, it)))
            if (list.indexOf(it) != list.size - 1) builder.appendLegacy("\n")
            if (Config.getBool("settings.reset-color-on-new-line")) builder.appendLegacy(ColorUtils.color("&r&f"))
        }

        return builder
    }
}