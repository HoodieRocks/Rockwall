package me.cobble.rockwall.utils

import me.clip.placeholderapi.PlaceholderAPI
import me.cobble.rockwall.config.Config
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.*
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.entity.Player
import java.util.regex.Matcher
import java.util.regex.Pattern

object TextUtils {
    private val hexPattern: Pattern = Pattern.compile("<#([A-Fa-f0-9]){6}>")
    var placeholderAPIPresent = false

    fun color(message: String): String {
        var message = message
        var matcher: Matcher = hexPattern.matcher(message)
        while (matcher.find()) {
            val hexColor: ChatColor = ChatColor.of(matcher.group().substring(1, matcher.group().length - 1))
            val before = message.substring(0, matcher.start())
            val after: String = message.substring(matcher.end())
            message = before + hexColor + after
            matcher = hexPattern.matcher(message)
        }
        return ChatColor.translateAlternateColorCodes('&', message)
    }

    /**
     * Same as Formats#color(text), but requires permission to use color
     * @see TextUtils.color
     */
    fun color(text: String, player: Player): String {
        if (player.hasPermission("rockwall.color")) return color(text)
        return text
    }

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
    private fun addEvents(text: String?, hoverText: String?, command: String?): BaseComponent {
        val component = TextComponent(text)
        component.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text(hoverText))
        component.clickEvent = ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command)
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
            builder.appendLegacy(color(setPlaceholders(player, it)))
            if (list.indexOf(it) != list.size - 1) builder.appendLegacy("\n")
            if (Config.getBool("settings.reset-color-on-new-line")) builder.appendLegacy(color("&r&f"))
        }

        return builder
    }

    /**
     * Uses PlaceholderAPI placeholders if present,
     * if not it returns the inputted string
     */
    fun setPlaceholders(player: Player, string: String): String {
        return if (placeholderAPIPresent) PlaceholderAPI.setPlaceholders(player, string)
        else string
    }
}
