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
        get() = "[label] leave <party name>"

    override fun run(p: Player, args: Array<String>) {
        if (args.isEmpty()) {
            p.sendMessage(Utils.color("&c$syntax"))
            return
        }

        if (!PartyUtils.validatePartyName(args[0])) {
            p.sendMessage(Messages.getPartyMsg("errors.invalid"))
            return
        }

        val party = PartyManager.getParty(args[0])

        if (party == null) {
            p.sendMessage(Messages.getPartyMsg("errors.404"))
            return
        }

        if (!party.isMember(p.uniqueId)) {
            p.sendMessage(Messages.getPartyMsg("errors.leave-party-not-in"))
            return
        }

        if (party.owner == p.uniqueId) {
            p.sendMessage(Messages.getPartyMsg("owner-leave-party"))
            return
        }

        party.members.remove(p.uniqueId)
        party.activeSpeakers.remove(p.uniqueId)
        p.sendMessage(Messages.getPartyMsg("leave"))

        if (party.members.size == 0) {
            PartyManager.deleteParty(party)
        }
    }
}
