package me.cobble.rockwall.cmds.parties.subcmds

import me.cobble.rockwall.config.models.Messages
import me.cobble.rockwall.utils.TextUtils
import me.cobble.rockwall.utils.models.RockwallBaseCommand
import me.cobble.rockwall.utils.parties.PartyManager
import me.cobble.rockwall.utils.parties.PartyUtils
import me.cobble.rockwall.utils.parties.models.AdminParty
import me.cobble.rockwall.utils.parties.models.Party
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class InviteToPartySub : RockwallBaseCommand {
    override val name: String
        get() = "invite"
    override val descriptor: String
        get() = "Invite players (Requires ownership)"
    override val syntax: String
        get() = "[label] invite <player> <player> ..."

    override fun run(p: Player, args: Array<String>): Boolean {
        if (args.isEmpty()) {
            p.sendMessage(TextUtils.color("&c${syntax.replace("[label]", "/party")}"))
            return false
        }

        val party: Party? = PartyManager.getParty(p.uniqueId)

        if (party == null) {
            p.sendMessage(Messages.getPartyMsg("errors.no-party-for-invites"))
            return false
        }

        if (party is AdminParty) {
            p.sendMessage(Messages.getPartyMsg("errors.cant-invite-to-admin-party"))
            return false
        }

        for (targetName in args) {
            val target = Bukkit.getPlayer(targetName)
            if (target == null) {
                p.sendMessage(Messages.getPartyMsg("errors.offline-player"))
                return false
            }

            if (party.owner == p.uniqueId) {
                if (party.isMember(target.uniqueId)) {
                    p.sendMessage(Messages.getPartyMsg("errors.already-a-member"))
                    return false
                }
                party.addInvite(target.uniqueId)
                PartyUtils.sendInvites(party.invites, party.alias)
                return true
            } else {
                p.sendMessage(Messages.getPermissionString("no-perm-party"))
                return false
            }
        }
        return false
    }
}
