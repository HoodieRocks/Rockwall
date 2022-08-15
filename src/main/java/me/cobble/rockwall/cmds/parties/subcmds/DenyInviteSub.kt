package me.cobble.rockwall.cmds.parties.subcmds

import me.cobble.rockwall.config.Messages
import me.cobble.rockwall.utils.Formats
import me.cobble.rockwall.utils.RockwallBaseCommand
import me.cobble.rockwall.utils.parties.Parties
import me.cobble.rockwall.utils.parties.PartyManager
import org.bukkit.entity.Player

class DenyInviteSub : RockwallBaseCommand() {
    override val name: String
        get() = "deny"
    override val descriptor: String
        get() = "Denies pending invites"
    override val syntax: String
        get() = "[label] deny <party name>"

    override fun run(p: Player, args: Array<String>) {
        if (args.isEmpty()) {
            p.sendMessage(Formats.color("&c${syntax.replace("[label]", "/party")}"))
        } else {
            if (Parties.validatePartyName(args[0]) && PartyManager.partyExists(args[0])) {
                val party = PartyManager.getParty(args[0])
                if (party!!.isInvited(p.uniqueId)) {
                    party.removeInvite(p.uniqueId)
                    p.sendMessage(Messages.getPartyMsg("deny"))
                } else {
                    p.sendMessage(Messages.getPartyMsg("errors.not-invited"))
                }
            }
        }
    }
}
