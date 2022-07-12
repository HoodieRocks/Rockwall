package me.cobble.rockwall.utils.groups

import me.cobble.rockwall.rockwall.Config
import me.cobble.rockwall.utils.Utils
import me.cobble.rockwall.utils.global.FormatType
import me.cobble.rockwall.utils.groups.models.Group
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

object GroupUtils {

    /**
     * Check to see if the group name is valid
     */
    fun validateGroupName(string: String): Boolean {
        return Regex("[A-z]+#[0-9]+").matches(string)
    }

    /**
     * Converts invited players to a group member
     */
    fun inviteToMember(uuid: UUID, group: Group?) {
        if (group == null) return
        group.addMember(uuid)
        group.removeInvite(uuid)
    }

    /**
     * Change what chat the player is in
     */
    fun changeChatSpeaker(player: UUID, group: Group?) {
        GroupManager.getGroups().values.forEach {
            if (group == null || it != group) {
                it.removeSpeaker(player)
            }
        }
    }

    /**
     * Get chat player is speaking in
     */
    fun getCurrentSpeakingChat(player: UUID): Group? {
        for (group: Group in GroupManager.getGroups().values) {
            if (group.isSpeaking(player)) {
                return group
            }
        }
        return null
    }

    fun formatMaker(player: Player, group: Group?, groupType: GroupType, formatType: FormatType): TextComponent? {
        val configSection =
            Config.get()!!.getConfigurationSection("groups.formats.${groupType.getType()}") ?: return null
        val section = configSection.getConfigurationSection(formatType.getType())
        val format = TextComponent(
            *TextComponent.fromLegacyText(
                Utils.color(
                    Utils.setPlaceholders(
                        player,
                        section!!.getString("display")!!.replace("%chat_alias%", group!!.alias)
                    )
                )
            )
        )
        format.hoverEvent = HoverEvent(
            HoverEvent.Action.SHOW_TEXT, Text(
                Utils.color(
                    Utils.setPlaceholders(
                        player,
                        Utils.flattenStringList(section.getStringList("hover"))
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
     * Check if groups are enabled in config
     */
    fun areGroupsEnabled(): Boolean {
        return Config.getBool("groups.enabled")
    }

    /**
     * Gets the user's groups
     * @return all groups the user is a member in
     */
    fun getUsersGroups(uuid: UUID): List<Group> {
        val player = Bukkit.getPlayer(uuid)
        val groups = ArrayList<Group>()

        if (player!!.hasPermission("rockwall.admin.join")) {
            return GroupManager.getGroups().values.toList()
        }

        for (group: Group in GroupManager.getGroups().values) {
            if (group.isMember(uuid)) groups.add(group)
        }

        return groups
    }
}