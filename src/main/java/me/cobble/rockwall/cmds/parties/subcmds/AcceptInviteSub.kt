package me.cobble.rockwall.cmds.parties.subcmds

import me.cobble.rockwall.config.Messages
import me.cobble.rockwall.utils.RockwallBaseCommand
import me.cobble.rockwall.utils.Utils
import me.cobble.rockwall.utils.parties.PartyManager
import me.cobble.rockwall.utils.parties.PartyUtils
import org.bukkit.entity.Player

class AcceptInviteSub : RockwallBaseCommand() {
    override val name: String
        get() = "accept"
    override val descriptor: String
        get() = "Accepts pending invites"
    override val syntax: String
        get() = "/party accept <group name>"

    override fun run(p: Player, args: Array<String>) {
        if (args.isEmpty()) {
            p.sendMessage(Utils.color("&c$syntax"))
        } else {
            if (PartyUtils.validatePartyName(args[0]) && PartyManager.partyExists(args[0])) {
                val group = PartyManager.getParty(args[0])
                if (group!!.isInvited(p.uniqueId)) {
                    PartyUtils.convertInviteToMember(p.uniqueId, group)
                    p.sendMessage(Messages.getPartyMsg("joined", group))
                }
            }
        }
    }
}