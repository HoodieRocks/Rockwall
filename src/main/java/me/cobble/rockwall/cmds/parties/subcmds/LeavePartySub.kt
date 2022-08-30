package me.cobble.rockwall.cmds.parties.subcmds

import me.cobble.rockwall.config.Messages
import me.cobble.rockwall.utils.TextUtils
import me.cobble.rockwall.utils.models.RockwallBaseCommand
import me.cobble.rockwall.utils.parties.Parties
import me.cobble.rockwall.utils.parties.PartyManager
import org.bukkit.entity.Player

class LeavePartySub : RockwallBaseCommand {
    override val name: String
        get() = "leave"
    override val descriptor: String
        get() = "Allows members (but not owners) to leave the chat"
    override val syntax: String
        get() = "[label] leave <party name>"

    override fun run(p: Player, args: Array<String>) {
        if (args.isEmpty()) {
            p.sendMessage(TextUtils.color("&c${syntax.replace("[label]", "/party")}"))
            return
        }

        if (!Parties.isPartyNameValid(args[0])) {
            p.sendMessage(Messages.getPartyMsg("errors.invalid"))
            return
        }

        PartyManager.getParty(args[0]).thenAccept {
            if (it == null) {
                p.sendMessage(Messages.getPartyMsg("errors.404"))
                return@thenAccept
            }

            if (!it.isMember(p.uniqueId)) {
                p.sendMessage(Messages.getPartyMsg("errors.leave-party-not-in"))
                return@thenAccept
            }

            if (it.owner == p.uniqueId) {
                p.sendMessage(Messages.getPartyMsg("errors.owner-leave-party"))
                return@thenAccept
            }

            it.members.remove(p.uniqueId)
            it.activeSpeakers.remove(p.uniqueId)
            p.sendMessage(Messages.getPartyMsg("leave"))

            if (it.members.size == 0) {
                PartyManager.deleteParty(it)
            }
        }

    }
}
