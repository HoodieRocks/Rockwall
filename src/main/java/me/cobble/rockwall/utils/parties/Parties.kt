package me.cobble.rockwall.utils.parties

import me.cobble.rockwall.config.Config
import me.cobble.rockwall.config.models.ChatFormatType
import me.cobble.rockwall.config.models.PartyType
import me.cobble.rockwall.utils.TextUtils
import me.cobble.rockwall.utils.parties.parties.Party
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

object Parties {

    fun isPartyNameValid(string: String): Boolean {
        return Regex("[A-z]+#[0-9]+").matches(string)
    }

    fun inviteToMember(uuid: UUID, party: Party?) {
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

    fun formatMaker(
        player: Player,
        party: Party?,
        partyType: PartyType,
        chatFormatType: ChatFormatType
    ): TextComponent? {
        val formatRoot = Config.getSection("parties.formats.${partyType.getType()}") ?: return null
        val section = formatRoot.getSection(chatFormatType.getType())
        val format = TextComponent(
            *TextComponent.fromLegacyText(
                TextUtils.color(
                    TextUtils.setPlaceholders(
                        player,
                        customPlaceholders(section!!.getString("display")!!, party!!)
                    )
                )
            )
        )
        format.font = section.getOptionalString("font").orElse("minecraft:default")
        format.hoverEvent = HoverEvent(
            HoverEvent.Action.SHOW_TEXT,
            Text(
                TextUtils.color(
                    TextUtils.setPlaceholders(
                        player,
                        customPlaceholders(
                            TextComponent.toPlainText(
                                *TextUtils.formatStringList(
                                    section.getStringList("hover"),
                                    player
                                ).create()
                            ), party
                        )
                    )
                )
            )
        )
        format.clickEvent = ClickEvent(
            ClickEvent.Action.SUGGEST_COMMAND,
            TextUtils.setPlaceholders(player, customPlaceholders(section.getString("on-click")!!, party))
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
        return string.replace("%party_alias%", party.alias, true)
    }

    fun sendInvites(invites: ArrayList<UUID>, name: String) {
        for (id: UUID in invites) {
            val player = Bukkit.getPlayer(id)
            val inviteComponent =
                TextComponent(TextUtils.color("&eYou've been invited to join party &7$name. \n&eClick accept to join, or click deny to decline.\n"))
            val accept = TextComponent(TextUtils.color("&a&lAccept"))
            val separator = TextComponent(TextUtils.color(" &8| "))
            val deny = TextComponent(TextUtils.color("&c&lDeny"))

            accept.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("Click to join $name"))
            deny.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("Click to decline"))

            accept.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/g accept $name")
            deny.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/g deny $name")

            inviteComponent.addExtra(accept)
            inviteComponent.addExtra(separator)
            inviteComponent.addExtra(deny)

            player!!.spigot().sendMessage(inviteComponent)
        }
    }
}
