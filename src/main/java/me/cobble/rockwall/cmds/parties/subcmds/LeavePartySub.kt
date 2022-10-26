package me.cobble.rockwall.cmds.parties.subcmds

import me.cobble.rockwall.config.models.Messages
import me.cobble.rockwall.utils.ColorUtils
import me.cobble.rockwall.utils.models.RockwallBaseCommand
import me.cobble.rockwall.utils.parties.PartyManager
import me.cobble.rockwall.utils.parties.PartyUtils
import org.bukkit.entity.Player

class LeavePartySub : RockwallBaseCommand {
    override val name: String
        get() = "leave"
    override val descriptor: String
        get() = "Allows members (but not owners) to leave the chat"
    override val syntax: String
        get() = "[label] leave <party name>"

    override fun run(p: Player, args: Array<String>): Boolean {
        if (args.isEmpty()) {
            p.sendMessage(ColorUtils.color("&c${syntax.replace("[label]", "/party")}"))
            return false
        }

        if (!PartyUtils.isPartyNameValid(args[0])) {
            p.sendMessage(Messages.getPartyMsg("errors.invalid"))
            return false
        }

        var returnBoolean = false
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
            returnBoolean = true
        }
        return returnBoolean
    }
}
