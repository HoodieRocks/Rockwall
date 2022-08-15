package me.cobble.rockwall.cmds.parties.subcmds

import me.cobble.rockwall.config.Messages
import me.cobble.rockwall.utils.Formats
import me.cobble.rockwall.utils.RockwallBaseCommand
import me.cobble.rockwall.utils.parties.Parties
import me.cobble.rockwall.utils.parties.PartyManager
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class PartyMembersSub : RockwallBaseCommand() {
    override val name: String
        get() = "members"
    override val descriptor: String
        get() = "Shows your party's members"
    override val syntax: String
        get() = "[label] members"

    override fun run(p: Player, args: Array<String>) {
        if (args.isEmpty()) {
            p.sendMessage(Formats.color("&c${syntax.replace("[label]", "/party")}"))
            return
        }

        if (args.size == 1) {
            if (!Parties.isPartyNameValid(args[0])) {
                p.sendMessage(Messages.getPartyMsg("errors.invalid"))
                return
            }

            val party = PartyManager.getParty(args[0])

            if (party == null) {
                p.sendMessage(Messages.getPartyMsg("errors.404"))
                return
            }

            if (!party.isMember(p.uniqueId)) {
                p.sendMessage(Messages.getPermissionString("no-perm-party"))
                return
            }

            p.sendMessage(Formats.color("\n\n&e&lParty &7Members\n\n"))
            p.sendMessage(Formats.color("&7(&cOwner&7) &f${Bukkit.getPlayer(party.owner)!!.name}"))
            for (member in party.members) {
                if (party.owner == member) return
                p.sendMessage(Formats.color("&7${Bukkit.getPlayer(member)!!.name}"))
            }
        }
    }
}
