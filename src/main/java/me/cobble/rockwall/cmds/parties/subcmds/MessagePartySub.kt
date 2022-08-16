package me.cobble.rockwall.cmds.parties.subcmds

import me.cobble.rockwall.config.Messages
import me.cobble.rockwall.utils.RockwallBaseCommand
import me.cobble.rockwall.utils.parties.Parties
import me.cobble.rockwall.utils.parties.PartyManager
import me.cobble.rockwall.utils.parties.parties.AdminParty
import org.bukkit.entity.Player

class MessagePartySub : RockwallBaseCommand() {
    override val name: String
        get() = "msg"
    override val descriptor: String
        get() = "Message your party"
    override val syntax: String
        get() = "[label] msg <party name>"

    override fun run(p: Player, args: Array<String>) {
        if (args.isEmpty() || (args.size == 1 && args[0].equals("global", ignoreCase = true))) {
            p.sendMessage(Messages.getPartyMsg("messaging-global"))
            Parties.removeOldSpeakingParty(p.uniqueId, null)
        } else {
            val partyName = args[0]
            if (Parties.isPartyNameValid(partyName)) {
                val party = PartyManager.getParty(partyName)

                if (!party!!.isMember(p.uniqueId)) {
                    p.sendMessage(Messages.getPermissionString("no-perm-party"))
                }

                if (PartyManager.partyExists(partyName) && ((party is AdminParty && p.hasPermission("rockwall.admin.join")) || party.isMember(
                        p.uniqueId
                    ))
                ) {
                    if (party.isSpeaking(p.uniqueId)) p.sendMessage(Messages.getPartyMsg("already-speaking")) else {
                        party.addSpeaker(p.uniqueId)
                        Parties.removeOldSpeakingParty(p.uniqueId, party)
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
