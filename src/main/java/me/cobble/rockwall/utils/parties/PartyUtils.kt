package me.cobble.rockwall.utils.parties

import me.cobble.rockwall.config.Config
import me.cobble.rockwall.utils.Utils
import me.cobble.rockwall.utils.global.FormatType
import me.cobble.rockwall.utils.parties.models.Party
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

object PartyUtils {

    /**
     * Check to see if the group name is valid
     */
    fun validateGroupName(string: String): Boolean {
        return Regex("[A-z]+#[0-9]+").matches(string)
    }

    /**
     * Converts invited players to a group member
     */
    fun inviteToMember(uuid: UUID, party: Party?) {
        if (party == null) return
        party.addMember(uuid)
        party.removeInvite(uuid)
    }

    /**
     * Change what chat the player is in
     */
    fun changeChatSpeaker(player: UUID, party: Party?) {
        PartyManager.getGroups().values.forEach {
            if (party == null || it != party) {
                it.removeSpeaker(player)
            }
        }
    }

    /**
     * Get chat player is speaking in
     */
    fun getCurrentSpeakingChat(player: UUID): Party? {
        for (party: Party in PartyManager.getGroups().values) {
            if (party.isSpeaking(player)) {
                return party
            }
        }
        return null
    }

    fun formatMaker(player: Player, party: Party?, partyType: PartyType, formatType: FormatType): TextComponent? {
        val configSection =
            Config.get()!!.getConfigurationSection("groups.formats.${partyType.getType()}") ?: return null
        val section = configSection.getConfigurationSection(formatType.getType())
        val format = TextComponent(
            *TextComponent.fromLegacyText(
                Utils.color(
                    Utils.setPlaceholders(
                        player,
                        section!!.getString("display")!!.replace("%chat_alias%", party!!.alias)
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
    fun arePartiesEnabled(): Boolean {
        return Config.getBool("groups.enabled")
    }

    /**
     * Gets the user's groups
     * @return all groups the user is a member in
     */
    fun getUsersGroups(uuid: UUID): List<Party> {
        val player = Bukkit.getPlayer(uuid)
        val parties = ArrayList<Party>()

        if (player!!.hasPermission("rockwall.admin.join")) {
            return PartyManager.getGroups().values.toList()
        }

        for (party: Party in PartyManager.getGroups().values) {
            if (party.isMember(uuid)) parties.add(party)
        }

        return parties
    }
}