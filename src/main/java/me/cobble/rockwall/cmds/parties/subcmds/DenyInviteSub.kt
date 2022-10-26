package me.cobble.rockwall.cmds.parties.subcmds

import me.cobble.rockwall.config.models.Messages
import me.cobble.rockwall.utils.ColorUtils
import me.cobble.rockwall.utils.models.RockwallBaseCommand
import me.cobble.rockwall.utils.parties.PartyManager
import me.cobble.rockwall.utils.parties.PartyUtils
import org.bukkit.entity.Player

class DenyInviteSub : RockwallBaseCommand {
    override val name: String
        get() = "deny"
    override val descriptor: String
        get() = "Denies pending invites"
    override val syntax: String
        get() = "[label] deny <party name>"

    override fun run(p: Player, args: Array<String>): Boolean {
        if (args.isEmpty()) {
            p.sendMessage(ColorUtils.color("&c${syntax.replace("[label]", "/party")}"))
            return false
        } else {
            if (PartyUtils.isPartyNameValid(args[0]) && PartyManager.doesPartyExists(args[0])) {
                var returnBool = false
                PartyManager.getParty(args[0]).thenAccept {
                    if (it!!.isInvited(p.uniqueId)) {
                        it.removeInvite(p.uniqueId)
                        p.sendMessage(Messages.getPartyMsg("deny"))
                        returnBool = true
                    } else {
                        p.sendMessage(Messages.getPartyMsg("errors.not-invited"))
                    }
                }
                return returnBool
            }
            return false
        }
    }
}
