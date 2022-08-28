package me.cobble.rockwall.cmds.parties.subcmds

import me.cobble.rockwall.config.Messages
import me.cobble.rockwall.utils.RockwallBaseCommand
import me.cobble.rockwall.utils.TextUtils
import me.cobble.rockwall.utils.parties.Parties
import me.cobble.rockwall.utils.parties.PartyManager
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class PartyMembersSub : RockwallBaseCommand {
    override val name: String
        get() = "members"
    override val descriptor: String
        get() = "Shows your party's members"
    override val syntax: String
        get() = "[label] members"

    override fun run(p: Player, args: Array<String>) {
        if (args.isEmpty()) {
            p.sendMessage(TextUtils.color("&c${syntax.replace("[label]", "/party")}"))
            return
        }

        if (args.size == 1) {
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
                    p.sendMessage(Messages.getPermissionString("no-perm-party"))
                    return@thenAccept
                }

                p.sendMessage(TextUtils.color("\n\n&e&lParty &7Members\n\n"))
                p.sendMessage(TextUtils.color("&7(&cOwner&7) &f${Bukkit.getPlayer(it.owner)!!.name}"))
                for (member in it.members) {
                    if (it.owner == member) return@thenAccept
                    p.sendMessage(TextUtils.color("&7${Bukkit.getPlayer(member)!!.name}"))
                }
            }
        }
    }
}
