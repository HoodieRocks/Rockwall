package me.cobble.rockwall.cmds.parties.subcmds

import me.cobble.rockwall.config.Messages
import me.cobble.rockwall.utils.RockwallBaseCommand
import me.cobble.rockwall.utils.Utils
import me.cobble.rockwall.utils.parties.PartyManager
import me.cobble.rockwall.utils.parties.PartyUtils
import org.bukkit.entity.Player

class DenyInviteSub(private val label: String) : RockwallBaseCommand() {
    override val name: String
        get() = "deny"
    override val descriptor: String
        get() = "Denies pending invites"
    override val syntax: String
        get() = "[label] deny <party name>"

    override fun run(p: Player, args: Array<String>) {
        if (args.isEmpty()) {
            p.sendMessage(Utils.color("&c$syntax").replace("[label]", label))
        } else {
            if (PartyUtils.validatePartyName(args[0]) && PartyManager.partyExists(args[0])) {
                val party = PartyManager.getParty(args[0])
                if (party!!.isInvited(p.uniqueId)) {
                    party.removeInvite(p.uniqueId)
                    p.sendMessage(Messages.getPartyMsg("invite-denied"))
                }
            }
        }
    }
}