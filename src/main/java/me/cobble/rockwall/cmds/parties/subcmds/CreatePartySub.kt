package me.cobble.rockwall.cmds.parties.subcmds

import me.cobble.rockwall.config.Messages
import me.cobble.rockwall.config.models.PartyType
import me.cobble.rockwall.utils.RockwallBaseCommand
import me.cobble.rockwall.utils.TextUtils
import me.cobble.rockwall.utils.TextUtils.containsSpecialCharacters
import me.cobble.rockwall.utils.parties.PartyManager
import org.bukkit.entity.Player

class CreatePartySub : RockwallBaseCommand {
    override val name: String
        get() = "create"
    override val descriptor: String
        get() = "Creates a new party"
    override val syntax: String
        get() = "[label] create <name> [type]"

    override fun run(p: Player, args: Array<String>) {
        if (args.isEmpty()) {
            p.sendMessage(TextUtils.color("&c${syntax.replace("[label]", "/party")}"))
        } else {
            if (args[0].containsSpecialCharacters()) {
                p.sendMessage(Messages.getPartyMsg("errors.no-special-characters"))
                return
            }

            if (PartyManager.doesPartyExists(p.uniqueId)) {
                p.sendMessage(Messages.getPartyMsg("errors.party-limit-reached"))
                return
            }

            if (args.size == 2) {
                val type = PartyType.valueOf(args[1].uppercase())
                if (type == PartyType.ADMIN && !p.hasPermission("rockwall.admin.create")) {
                    p.sendMessage(Messages.getPermissionString("no-admin-create-perm"))
                    return
                }
                PartyManager.createParty(p.uniqueId, args[0], PartyType.valueOf(args[1].uppercase().trim()))
            } else {
                PartyManager.createParty(p.uniqueId, args[0], PartyType.NORMAL)
            }
            PartyManager.getParty(p.uniqueId)!!.addMember(p.uniqueId)

            p.sendMessage(Messages.getPartyMsg("creation", PartyManager.getParty(p.uniqueId)!!))
        }
    }
}
