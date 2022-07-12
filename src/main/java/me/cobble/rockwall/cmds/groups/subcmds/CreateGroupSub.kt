package me.cobble.rockwall.cmds.groups.subcmds

import me.cobble.rockwall.rockwall.Messages
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
                p.sendMessage(Messages.getGroupString("errors.no-special-characters"))
                return
            }

            if (GroupManager.groupExists(p.uniqueId)) {
                p.sendMessage(Messages.getGroupString("errors.group-limit-reached"))
                return
            }

            if (args.size == 2) {
                val type = GroupType.valueOf(args[1].uppercase())
                if (type == GroupType.ADMIN && !p.hasPermission("rockwall.admin.create")) {
                    p.sendMessage(Messages.getPermissionString("no-admin-create-perm"))
                    return
                }
                GroupManager.createGroup(p.uniqueId, args[0], GroupType.valueOf(args[1].uppercase().trim()))
            } else {
                GroupManager.createGroup(p.uniqueId, args[0], GroupType.NORMAL)
            }
            GroupManager.getGroup(p.uniqueId)!!.addMember(p.uniqueId)

            p.sendMessage(Messages.getGroupString("creation", GroupManager.getGroup(p.uniqueId)!!))
        }
    }
}