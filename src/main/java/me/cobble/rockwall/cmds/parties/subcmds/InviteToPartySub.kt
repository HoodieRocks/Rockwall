package me.cobble.rockwall.cmds.parties.subcmds

import me.cobble.rockwall.config.Messages
import me.cobble.rockwall.utils.RockwallBaseCommand
import me.cobble.rockwall.utils.TextUtils
import me.cobble.rockwall.utils.parties.Parties
import me.cobble.rockwall.utils.parties.PartyManager
import me.cobble.rockwall.utils.parties.parties.AdminParty
import me.cobble.rockwall.utils.parties.parties.Party
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class InviteToPartySub : RockwallBaseCommand {
    override val name: String
        get() = "invite"
    override val descriptor: String
        get() = "Invite players (Requires ownership)"
    override val syntax: String
        get() = "[label] invite <player> <player> ..."

    override fun run(p: Player, args: Array<String>) {
        if (args.isEmpty()) {
            p.sendMessage(TextUtils.color("&c${syntax.replace("[label]", "/party")}"))
            return
        }

        val party: Party? = PartyManager.getParty(p.uniqueId)

        if (party == null) {
            p.sendMessage(Messages.getPartyMsg("errors.no-party-for-invites"))
            return
        }

        if (party is AdminParty) {
            p.sendMessage(Messages.getPartyMsg("errors.cant-invite-to-admin-party"))
            return
        }

        for (targetName in args) {
            val target = Bukkit.getPlayer(targetName)
            if (target == null) {
                p.sendMessage(Messages.getPartyMsg("errors.offline-player"))
                return
            }

            if (party.owner == p.uniqueId) {
                party.addInvite(target.uniqueId)
                Parties.sendInvites(party.invites, party.alias)
            } else {
                p.sendMessage(Messages.getPermissionString("no-perm-party"))
                return
            }
        }
    }
}
