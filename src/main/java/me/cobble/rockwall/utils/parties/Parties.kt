package me.cobble.rockwall.utils.parties

import me.cobble.rockwall.config.Config
import me.cobble.rockwall.utils.Formats
import me.cobble.rockwall.utils.chat.FormatType
import me.cobble.rockwall.utils.parties.models.Party
import me.cobble.rockwall.utils.parties.models.PartyType
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

object Parties {

    fun validatePartyName(string: String): Boolean {
        return Regex("[A-z]+#[0-9]+").matches(string)
    }

    fun convertInviteToMember(uuid: UUID, party: Party?) {
        if (party == null) return
        party.addMember(uuid)
        party.removeInvite(uuid)
    }

    fun removeOldSpeakingParty(player: UUID, party: Party?) {
        PartyManager.getParties().values.forEach {
            if (party == null || it != party) {
                it.removeSpeaker(player)
            }
        }
    }

    fun getPartyBySpeaking(player: UUID): Party? {
        for (party: Party in PartyManager.getParties().values) {
            if (party.isSpeaking(player)) {
                return party
            }
        }
        return null
    }

    fun formatMaker(player: Player, party: Party?, partyType: PartyType, formatType: FormatType): TextComponent? {
        val formatRoot = Config.getSection("parties.formats.${partyType.getType()}") ?: return null
        val section = formatRoot.getSection(formatType.getType())
        val format = TextComponent(
            *TextComponent.fromLegacyText(
                Formats.color(
                    Formats.setPlaceholders(
                        player,
                        customPlaceholders(section!!.getString("display")!!, party!!)
                    )
                )
            )
        )
        format.hoverEvent = HoverEvent(
            HoverEvent.Action.SHOW_TEXT,
            Text(
                Formats.color(
                    Formats.setPlaceholders(
                        player,
                        customPlaceholders(Formats.flattenList(section.getStringList("hover")), party)
                    )
                )
            )
        )
        format.clickEvent = ClickEvent(
            ClickEvent.Action.SUGGEST_COMMAND,
            Formats.setPlaceholders(player, customPlaceholders(section.getString("on-click")!!, party))
        )

        return format
    }

    /**
     * Check if parties are enabled in config
     */
    fun arePartiesEnabled(): Boolean {
        return Config.getBool("parties.enabled")
    }

    /**
     * Gets the user's parties
     * @return all parties the user is a member in
     */
    fun getUserParties(uuid: UUID): List<Party> {
        val player = Bukkit.getPlayer(uuid)
        val parties = ArrayList<Party>()

        if (player!!.hasPermission("rockwall.admin.join")) {
            return PartyManager.getParties().values.toList()
        }

        for (party: Party in PartyManager.getParties().values) {
            if (party.isMember(uuid)) parties.add(party)
        }

        return parties
    }

    private fun customPlaceholders(string: String, party: Party): String {
        return string.replace("%party_alias%", party.alias)
    }
}
