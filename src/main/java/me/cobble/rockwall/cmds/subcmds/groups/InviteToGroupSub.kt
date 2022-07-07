package me.cobble.rockwall.cmds.subcmds.groups

import me.cobble.rockwall.utils.RockwallBaseCommand
import me.cobble.rockwall.utils.Utils
import me.cobble.rockwall.utils.groups.GroupManager
import me.cobble.rockwall.utils.groups.InviteSender
import me.cobble.rockwall.utils.groups.models.Group
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class InviteToGroupSub : RockwallBaseCommand() {
    override val name: String
        get() = "invite"
    override val descriptor: String
        get() = "Invites players to your group \n(you must be the owner of the group to do this)"
    override val syntax: String
        get() = "/g invite <player>"

    override fun run(p: Player, args: Array<String>) {
        if (args.isEmpty()) {
            p.sendMessage(Utils.color("&c$syntax"))
            return
        }

        val group: Group? = GroupManager.getGroup(p.uniqueId)
        val target = Bukkit.getPlayer(args[0])

        if (group == null) {
            p.sendMessage(Utils.color("&cYou do not have a chat to invite people to!"))
            return
        }

        if (target == null) {
            p.sendMessage(Utils.color("&cThat player does not exist or is offline"))
        }

        if (group.owner == p.uniqueId) {
            group.invites.add(target!!.uniqueId)
            InviteSender.sendInvites(group.invites, group.alias)
        } else {
            p.sendMessage(Utils.color("&cYou do not have permission to invite others to this chat"))
        }
    }
}