package me.cobble.rockwall.utils

import me.clip.placeholderapi.PlaceholderAPI
import me.cobble.rockwall.config.Config
import me.cobble.rockwall.utils.models.RockwallBaseCommand
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.*
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.entity.Player

object TextUtils {
    var placeholderAPIPresent = false
    private const val WITH_DELIMITER = "((?<=%1\$s)|(?=%1\$s))"

    /**
     * @param text The string of text to apply color/effects to
     * @return Returns a string of text with color/effects applied
     */
    fun color(text: String): String {
        val texts = text.split(String.format(WITH_DELIMITER, "&").toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        val finalText = StringBuilder()
        var i = 0
        while (i < texts.size) {
            if (texts[i].equals("&", ignoreCase = true)) {
                //get the next string
                i++
                if (texts[i][0] == '#') {
                    finalText.append(ChatColor.of(texts[i].substring(0, 7)).toString() + texts[i].substring(7))
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


    fun colorToTextComponent(text: String): TextComponent {
        val texts: Array<String> =
            text.split(java.lang.String.format(WITH_DELIMITER, "&").toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
        val builder = ComponentBuilder()
        var i = 0
        while (i < texts.size) {
            val subComponent = TextComponent()
            if (texts[i].equals("&", ignoreCase = true)) {
                //get the next string
                i++
                if (texts[i][0] == '#') {
                    subComponent.text = texts[i].substring(7)
                    subComponent.color = ChatColor.of(texts[i].substring(0, 7))
                    builder.append(subComponent)
                } else {
                    if (texts[i].length > 1) {
                        subComponent.text = texts[i].substring(1)
                    } else {
                        subComponent.text = ""
                    }
                    when (texts[i][0]) {
                        '0' -> subComponent.color = ChatColor.BLACK
                        '1' -> subComponent.color = ChatColor.DARK_BLUE
                        '2' -> subComponent.color = ChatColor.DARK_GREEN
                        '3' -> subComponent.color = ChatColor.DARK_AQUA
                        '4' -> subComponent.color = ChatColor.DARK_RED
                        '5' -> subComponent.color = ChatColor.DARK_PURPLE
                        '6' -> subComponent.color = ChatColor.GOLD
                        '7' -> subComponent.color = ChatColor.GRAY
                        '8' -> subComponent.color = ChatColor.DARK_GRAY
                        '9' -> subComponent.color = ChatColor.BLUE
                        'a' -> subComponent.color = ChatColor.GREEN
                        'b' -> subComponent.color = ChatColor.AQUA
                        'c' -> subComponent.color = ChatColor.RED
                        'd' -> subComponent.color = ChatColor.LIGHT_PURPLE
                        'e' -> subComponent.color = ChatColor.YELLOW
                        'f' -> subComponent.color = ChatColor.WHITE
                        'k' -> subComponent.isObfuscated = true
                        'l' -> subComponent.isBold = true
                        'm' -> subComponent.isStrikethrough = true
                        'n' -> subComponent.isUnderlined = true
                        'o' -> subComponent.isItalic = true
                        'r' -> subComponent.color = ChatColor.RESET
                    }
                    builder.append(subComponent)
                }
            } else {
                builder.append(texts[i])
            }
            i++
        }
        return TextComponent(*builder.create())
    }

    fun colorToTextComponent(string: String, player: Player): TextComponent {
        return if (player.hasPermission("rockwall.color")) colorToTextComponent(string)
        else {
            val component = TextComponent(string)
            component.retain(ComponentBuilder.FormatRetention.NONE)
            component
        }
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
    private fun addEvents(text: String, hoverText: String?, command: String?): BaseComponent {
        val component = colorToTextComponent(text)
        component.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text(hoverText))
        component.clickEvent = ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command)
        return component
    }

    private fun formatCmd(prefix: String, cmd: String): String = color("&e$prefix&7$cmd\n")

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
            builder.append(colorToTextComponent(setPlaceholders(player, it)))
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

    fun sendDebug(string: String, player: Player) {
        if (Config.isDebugEnabled()) {
            player.sendMessage(color("&e[DEBUG] $string"))
        }
    }
}
