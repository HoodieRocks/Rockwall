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
            if (PartyUtils.validateGroupName(args[0]) && PartyManager.groupExists(args[0])) {
                val group = PartyManager.getGroup(args[0])
                if (group!!.isInvited(p.uniqueId)) {
                    PartyUtils.inviteToMember(p.uniqueId, group)
                    p.sendMessage(Messages.getGroupString("joined", group))
                }
            }
        }
    }
}