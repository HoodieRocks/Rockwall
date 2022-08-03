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

class InviteToPartySub(private val label: String) : RockwallBaseCommand() {
    override val name: String
        get() = "invite"
    override val descriptor: String
        get() = "Invites players to your party (Must be party owner)"
    override val syntax: String
        get() = "[label] invite <name> <name>"

    override fun run(p: Player, args: Array<String>) {
        if (args.isEmpty()) {
            p.sendMessage(Utils.color("&c$syntax").replace("[label]", label))
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
                InviteSender.sendInvites(party.invites, party.alias)
            } else {
                p.sendMessage(Messages.getPermissionString("no-perm-party"))
                return
            }
        }
    }
}