package me.cobble.rockwall.cmds.parties.subcmds

import me.cobble.rockwall.config.models.Messages
import me.cobble.rockwall.utils.ColorUtils
import me.cobble.rockwall.utils.models.RockwallBaseCommand
import me.cobble.rockwall.utils.parties.PartyManager
import me.cobble.rockwall.utils.parties.PartyUtils
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class PartyMembersSub : RockwallBaseCommand {
    override val name: String
        get() = "members"
    override val descriptor: String
        get() = "Shows your party's members"
    override val syntax: String
        get() = "[label] members <party name>"

    override fun run(p: Player, args: Array<String>): Boolean {
        if (args.isEmpty()) {
            p.sendMessage(ColorUtils.color("&c${syntax.replace("[label]", "/party")}"))
            return false
        }

        if (args.size == 1) {
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
                    p.sendMessage(Messages.getPermissionString("no-perm-party"))
                    return@thenAccept
                }

                p.sendMessage(ColorUtils.color("\n\n&e&lParty &7Members\n\n"))
                p.sendMessage(ColorUtils.color("&7(&cOwner&7) &f${Bukkit.getPlayer(it.owner)!!.name}"))
                for (member in it.members) {
                    if (it.owner != member) p.sendMessage(ColorUtils.color("&7${Bukkit.getPlayer(member)!!.name}"))
                }
                returnBoolean = true
            }

            return returnBoolean
        } else {
            p.sendMessage(Messages.getGeneralError("too-many-args"))
            return false
        }
    }
}
