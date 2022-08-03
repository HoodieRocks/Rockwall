package me.cobble.rockwall.cmds.parties.subcmds

import me.cobble.rockwall.config.Messages
import me.cobble.rockwall.utils.RockwallBaseCommand
import me.cobble.rockwall.utils.parties.PartyManager
import me.cobble.rockwall.utils.parties.PartyUtils
import me.cobble.rockwall.utils.parties.models.AdminParty
import org.bukkit.entity.Player

class MessagePartySub(private val label: String) : RockwallBaseCommand() {
    override val name: String
        get() = "msg"
    override val descriptor: String
        get() = "Message your party"
    override val syntax: String
        get() = "[label] msg <party name>"

    override fun run(p: Player, args: Array<String>) {
        if (args.isEmpty() || (args.size == 1 && args[0].equals("global", ignoreCase = true))) {
            p.sendMessage(Messages.getPartyMsg("messaging-global"))
            PartyUtils.removeOldSpeakingParty(p.uniqueId, null)
        } else {
            val partyName = args[0]
            if (PartyUtils.validatePartyName(partyName)) {
                val party = PartyManager.getParty(partyName)
                if (PartyManager.partyExists(partyName) && ((party is AdminParty && p.hasPermission("rockwall.admin.join")) || party!!.isMember(
                        p.uniqueId
                    ))
                ) {
                    if (party.isSpeaking(p.uniqueId)) p.sendMessage(Messages.getPartyMsg("already-speaking")) else {
                        party.addSpeaker(p.uniqueId)
                        PartyUtils.removeOldSpeakingParty(p.uniqueId, party)
                        p.sendMessage(Messages.getPartyMsg("now-speaking", party))
                    }
                } else {
                    p.sendMessage(Messages.getPartyMsg("errors.404"))
                }
            } else {
                p.sendMessage(Messages.getPartyMsg("errors.invalid"))
            }
        }
    }
}