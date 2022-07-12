package me.cobble.rockwall.cmds.groups.subcmds

import me.cobble.rockwall.rockwall.Messages
import me.cobble.rockwall.utils.RockwallBaseCommand
import me.cobble.rockwall.utils.groups.GroupManager
import org.bukkit.entity.Player

class DeleteGroupSub : RockwallBaseCommand() {
    override val name: String
        get() = "delete"
    override val descriptor: String
        get() = "Deletes a group. \n(You must be the group owner to do this)"
    override val syntax: String
        get() = "/g delete"

    override fun run(p: Player, args: Array<String>) {
        val group = GroupManager.getGroup(p.uniqueId)

        if (group == null) {
            p.sendMessage(Messages.getGroupString("errors.no-group-for-deletion"))
            return
        }

        if (group.owner == p.uniqueId) {
            GroupManager.deleteGroup(group)
        }
    }
}