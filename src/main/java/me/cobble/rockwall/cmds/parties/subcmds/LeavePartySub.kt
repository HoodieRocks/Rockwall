package me.cobble.rockwall.cmds.parties.subcmds

import me.cobble.rockwall.config.Messages
import me.cobble.rockwall.utils.RockwallBaseCommand
import me.cobble.rockwall.utils.Utils
import me.cobble.rockwall.utils.parties.PartyManager
import me.cobble.rockwall.utils.parties.PartyUtils
import org.bukkit.entity.Player

class LeavePartySub : RockwallBaseCommand() {
    override val name: String
        get() = "leave"
    override val descriptor: String
        get() = "Allows members (but not owners) to leave the chat"
    override val syntax: String
        get() = "/party leave <group name>"

    override fun run(p: Player, args: Array<String>) {
        if (args.isEmpty()) {
            p.sendMessage(Utils.color("&c$syntax"))
            return
        }

        if (!PartyUtils.validatePartyName(args[0])) {
            p.sendMessage(Messages.getPartyMsg("errors.invalid"))
            return
        }

        val group = PartyManager.getParty(args[0])

        if (group == null) {
            p.sendMessage(Messages.getPartyMsg("errors.404"))
            return
        }

        if (!group.isMember(p.uniqueId)) {
            p.sendMessage(Messages.getPartyMsg("errors.leave-group-not-in"))
            return
        }

        if (group.owner == p.uniqueId) {
            p.sendMessage(Messages.getPartyMsg("owner-leave-group"))
            return
        }

        group.members.remove(p.uniqueId)
        group.activeSpeakers.remove(p.uniqueId)
        p.sendMessage(Messages.getPartyMsg("leave"))

        if (group.members.size == 0) {
            PartyManager.deleteParty(group)
        }
    }
}