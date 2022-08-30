package me.cobble.rockwall.cmds.parties.subcmds

import me.cobble.rockwall.config.models.Messages
import me.cobble.rockwall.utils.TextUtils
import me.cobble.rockwall.utils.models.RockwallBaseCommand
import me.cobble.rockwall.utils.parties.PartyUtils
import me.cobble.rockwall.utils.parties.PartyManager
import org.bukkit.entity.Player

class DenyInviteSub : RockwallBaseCommand {
    override val name: String
        get() = "deny"
    override val descriptor: String
        get() = "Denies pending invites"
    override val syntax: String
        get() = "[label] deny <party name>"

    override fun run(p: Player, args: Array<String>) {
        if (args.isEmpty()) {
            p.sendMessage(TextUtils.color("&c${syntax.replace("[label]", "/party")}"))
        } else {
            if (PartyUtils.isPartyNameValid(args[0]) && PartyManager.doesPartyExists(args[0])) {
                PartyManager.getParty(args[0]).thenAccept {
                    if (it!!.isInvited(p.uniqueId)) {
                        it.removeInvite(p.uniqueId)
                        p.sendMessage(Messages.getPartyMsg("deny"))
                    } else {
                        p.sendMessage(Messages.getPartyMsg("errors.not-invited"))
                    }
                }
            }
        }
    }
}
