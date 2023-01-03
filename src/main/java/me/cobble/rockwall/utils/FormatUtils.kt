package me.cobble.rockwall.utils

import me.cobble.rockwall.config.Config
import me.cobble.rockwall.utils.models.FormatTree
import me.cobble.rockwall.utils.models.RockwallBaseCommand
import me.cobble.rockwall.utils.parties.models.Party
import net.md_5.bungee.api.chat.*
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
        val component = ColorUtils.colorSpigot(text)
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
        if (list.isEmpty()) return ComponentBuilder()
        val builder = ComponentBuilder()
        val resetColor = Config.getBool("settings.reset-color-on-new-line")
        list.forEachIndexed { idx, str ->
            builder.append(ColorUtils.colorSpigot(ColorUtils.setPlaceholders(player, str)))
            if (idx != list.size - 1) builder.appendLegacy("\n")
            if (resetColor) builder.appendLegacy(ColorUtils.color("&r&f"))
        }

        return builder
    }

    fun assembleMessage(
        message: TextComponent,
        tree: FormatTree,
        treeKey: String,
        player: Player,
        party: Party? = null
    ): ComponentBuilder {

        val prefix = tree.asRockwallFormat(player, tree.getGroupPrefix(treeKey), party)
        val prefixSeparator = tree.asRockwallFormat(player, tree.getGroupPrefixSeparator(treeKey), party)
        val name = tree.asRockwallFormat(player, tree.getGroupPlayerName(treeKey), party)
        val nameSeparator = tree.asRockwallFormat(player, tree.getGroupNameSeparator(treeKey), party)
        val suffix = tree.asRockwallFormat(player, tree.getGroupSuffix(treeKey), party)
        val suffixSeparator = tree.asRockwallFormat(player, tree.getGroupSuffixSeparator(treeKey), party)

        return ComponentBuilder()
            .append(prefix)
            .append(prefixSeparator)
            .append(name)
            .append(nameSeparator)
            .append(suffix)
            .append(suffixSeparator)
            .append(message)
    }

    fun customPlaceholders(string: MutableList<String>, party: Party): MutableList<String> {
        val copy = string.toMutableList() // prevents a side effect
        copy.forEach {
            it.replace("%party_alias%", party.alias, true)
        }
        return copy
    }

    fun customPlaceholders(string: String, party: Party): String =
        string.replace("%party_alias%", party.alias, true)
}