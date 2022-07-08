package me.cobble.rockwall.utils

import me.clip.placeholderapi.PlaceholderAPI
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.entity.Player

object Utils {
    private const val WITH_DELIMITER = "((?<=%1\$s)|(?=%1\$s))"
    var placeholderAPIPresent = false

    fun color(text: String): String {
        val texts: Array<String> =
            text.split(String.format(WITH_DELIMITER, "&").toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()
        val finalText = StringBuilder()
        var i = 0
        while (i < texts.size) {
            if ("&".equals(texts[i], ignoreCase = true)) {
                //get the next string
                i++
                if (texts[i][0] == '#') {
                    finalText.append(ChatColor.of(texts[i].substring(0, 7))).append(texts[i].substring(7))
                } else {
                    finalText.append(ChatColor.translateAlternateColorCodes('&', "&" + texts[i]))
                }
            } else {
                finalText.append(texts[i])
            }
            i++
        }
        return finalText.toString()
    }

    /**
     * Same as Utils#color(text), but requires permission to use color
     * @see Utils.color
     */
    fun color(text: String, player: Player): String {
        if (player.hasPermission("rockwall.color")) {
            val texts: Array<String> =
                text.split(String.format(WITH_DELIMITER, "&").toRegex())
                    .dropLastWhile { it.isEmpty() }
                    .toTypedArray()
            val finalText = StringBuilder()
            var i = 0
            while (i < texts.size) {
                if ("&".equals(texts[i], ignoreCase = true)) {
                    //get the next string
                    i++
                    if (texts[i][0] == '#') {
                        finalText.append(ChatColor.of(texts[i].substring(0, 7))).append(texts[i].substring(7))
                    } else {
                        finalText.append(ChatColor.translateAlternateColorCodes('&', "&" + texts[i]))
                    }
                } else {
                    finalText.append(texts[i])
                }
                i++
            }
            return finalText.toString()
        }
        return text
    }

    /**
     * Formats sub commands as a file structure display
     */
    fun formatAsFileStructure(list: List<RockwallBaseCommand>): Array<BaseComponent> {
        val components = ArrayList<BaseComponent>()
        val sortedList = list.sortedBy { it.name }

        for (i in sortedList.indices) {
            val sc: RockwallBaseCommand = sortedList[i]
            val format = "&e${sc.syntax} &7- ${sc.descriptor}"
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
    private fun addEvents(text: String?, hoverText: String?, command: String?): BaseComponent {
        val component = TextComponent(text)
        component.hoverEvent =
            HoverEvent(HoverEvent.Action.SHOW_TEXT, Text(hoverText))
        component.clickEvent =
            ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command)
        return component
    }

    private fun formatCmd(prefix: String, cmd: String): String {
        return color("&e$prefix&7$cmd\n")
    }

    /**
     * Checks if string is non-alphanumeric
     * @return if string is alphanumeric
     */
    fun String.containsSpecialCharacters(): Boolean {
        return this.contains(Regex("[^A-Za-z0-9]"))
    }

    /**
     * Flattens a string list to a single string
     * @return string list as string
     */
    fun flattenStringList(list: List<String>): String {
        val builder: StringBuilder = StringBuilder()
        list.forEach {
            builder.append(it)
            if (list.indexOf(it) != list.size - 1) builder.append('\n')
        }

        return builder.toString()
    }

    fun setPlaceholders(player: Player, string: String): String {
        return if(placeholderAPIPresent) PlaceholderAPI.setPlaceholders(player, string)
        else string
    }
}