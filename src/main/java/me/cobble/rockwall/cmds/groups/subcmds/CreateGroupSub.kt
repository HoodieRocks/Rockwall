package me.cobble.rockwall.cmds.groups.subcmds

import me.cobble.rockwall.utils.RockwallBaseCommand
import me.cobble.rockwall.utils.Utils
import me.cobble.rockwall.utils.Utils.containsSpecialCharacters
import me.cobble.rockwall.utils.groups.GroupManager
import me.cobble.rockwall.utils.groups.GroupType
import org.bukkit.entity.Player

class CreateGroupSub : RockwallBaseCommand() {
    override val name: String
        get() = "create"
    override val descriptor: String
        get() = "Creates a new group"
    override val syntax: String
        get() = "/g create <name>"

    override fun run(p: Player, args: Array<String>) {
        if (args.isEmpty()) {
            p.sendMessage(Utils.color("&c$syntax"))
        } else {
            if (args[0].containsSpecialCharacters()) {
                p.sendMessage(Utils.color("&cYou can not use special characters in the group name!"))
                return
            }

            if (GroupManager.groupExists(p.uniqueId)) {
                p.sendMessage(Utils.color("&cYou can not have 2 groups open at the same time!"))
                return
            }

            if (args.size == 2) {
                val type = GroupType.valueOf(args[1].uppercase())
                if (type == GroupType.ADMIN && !p.hasPermission("rockwall.admin.create")) {
                    p.sendMessage(Utils.color("&cYou do not have permission to create an admin group"))
                    return
                }
                GroupManager.createGroup(p.uniqueId, args[0], GroupType.valueOf(args[1].uppercase().trim()))
            } else {
                GroupManager.createGroup(p.uniqueId, args[0], GroupType.NORMAL)
            }

            p.sendMessage(Utils.color("&aGroup created with name ${GroupManager.getGroup(p.uniqueId)!!.alias}"))
        }
    }
}