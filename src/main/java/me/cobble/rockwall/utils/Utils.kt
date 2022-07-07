package me.cobble.rockwall.utils

import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.entity.Player

object Utils {
    private const val WITH_DELIMITER = "((?<=%1\$s)|(?=%1\$s))"

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

    // same as Utils#color(text), but requires permission to use color
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

    fun formatAsFileStructure(array: List<RockwallBaseCommand>): Array<BaseComponent> {
        val components = ArrayList<BaseComponent>()

        for (i in array.indices) {
            val sc: RockwallBaseCommand = array[i]
            val format: String = "&e${sc.syntax} &7- ${sc.descriptor}"
            when (i) {
                0 -> components.add(addEvents(formatCmd("┌ ", format), sc.descriptor, sc.syntax)!!)
                array.size - 1 -> components.add(addEvents(formatCmd("└ ", format), sc.descriptor, sc.syntax)!!)
                else -> components.add(addEvents(formatCmd("├ ", format), sc.descriptor, sc.syntax)!!)
            }
        }
        return components.toTypedArray()
    }

    private fun addEvents(text: String?, hoverText: String?, command: String?): BaseComponent? {
        val component = TextComponent(text)
        component.hoverEvent =
            HoverEvent(HoverEvent.Action.SHOW_TEXT, Text(hoverText))
        component.clickEvent =
            ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command)
        return component
    }

    private fun formatCmd(prefix: String, cmd: String): String? {
        return color("&e$prefix&7$cmd\n")
    }

    fun String.containsSpecialCharacters(): Boolean {
        return this.contains(Regex("[^A-Za-z0-9]"))
    }
}