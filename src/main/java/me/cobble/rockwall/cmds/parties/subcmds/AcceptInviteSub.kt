package me.cobble.rockwall.cmds.parties.subcmds

import me.cobble.rockwall.config.Messages
import me.cobble.rockwall.utils.TextUtils
import me.cobble.rockwall.utils.models.RockwallBaseCommand
import me.cobble.rockwall.utils.parties.Parties
import me.cobble.rockwall.utils.parties.PartyManager
import org.bukkit.entity.Player

class AcceptInviteSub : RockwallBaseCommand {
    override val name: String
        get() = "accept"
    override val descriptor: String
        get() = "Accepts pending invites"
    override val syntax: String
        get() = "[label] accept <party name>"

    override fun run(p: Player, args: Array<String>) {
        if (args.isEmpty()) {
            p.sendMessage(TextUtils.color("&c${syntax.replace("[label]", "/party")}"))
        } else {
            if (Parties.isPartyNameValid(args[0]) && PartyManager.doesPartyExists(args[0])) {
                PartyManager.getParty(args[0]).thenAccept {

                    if (it!!.isInvited(p.uniqueId)) {
                        Parties.inviteToMember(p.uniqueId, it)
                        p.sendMessage(Messages.getPartyMsg("joined", it))
                    } else {
                        p.sendMessage(Messages.getPartyMsg("errors.not-invited"))
                    }
                }
            }
        }
    }
}
