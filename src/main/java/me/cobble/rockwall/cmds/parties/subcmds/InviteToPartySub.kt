package me.cobble.rockwall.cmds.parties.subcmds

import me.cobble.rockwall.config.Messages
import me.cobble.rockwall.utils.RockwallBaseCommand
import me.cobble.rockwall.utils.Utils
import me.cobble.rockwall.utils.parties.InviteSender
import me.cobble.rockwall.utils.parties.PartyManager
import me.cobble.rockwall.utils.parties.models.AdminParty
import me.cobble.rockwall.utils.parties.models.Party
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class InviteToPartySub : RockwallBaseCommand() {
    override val name: String
        get() = "invite"
    override val descriptor: String
        get() = "Invites players to your group \n(you must be the owner of the group to do this)"
    override val syntax: String
        get() = "/party invite <player> <player> ..."

    override fun run(p: Player, args: Array<String>) {
        if (args.isEmpty()) {
            p.sendMessage(Utils.color("&c$syntax"))
            return
        }

        val party: Party? = PartyManager.getGroup(p.uniqueId)

        if (party == null) {
            p.sendMessage(Messages.getGroupString("errors.no-group-for-invites"))
            return
        }

        if (party is AdminParty) {
            p.sendMessage(Messages.getGroupString("errors.cant-invite-to-admin-group"))
            return
        }

        for (targetName in args) {
            val target = Bukkit.getPlayer(targetName)
            if (target == null) {
                p.sendMessage(Messages.getGroupString("errors.offline-player"))
                return
            }

            if (party.owner == p.uniqueId) {
                party.addInvite(target.uniqueId)
                InviteSender.sendInvites(party.invites, party.alias)
            } else {
                p.sendMessage(Messages.getPermissionString("no-perm-group"))
                return
            }
        }
    }
}