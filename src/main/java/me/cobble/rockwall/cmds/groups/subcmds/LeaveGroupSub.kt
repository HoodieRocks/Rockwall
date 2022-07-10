package me.cobble.rockwall.cmds.groups.subcmds

import me.cobble.rockwall.utils.RockwallBaseCommand
import me.cobble.rockwall.utils.Utils
import me.cobble.rockwall.utils.groups.GroupManager
import me.cobble.rockwall.utils.groups.GroupUtils
import org.bukkit.entity.Player

class LeaveGroupSub : RockwallBaseCommand() {
    override val name: String
        get() = "leave"
    override val descriptor: String
        get() = "Allows members (but not owners) to leave the chat"
    override val syntax: String
        get() = "/g leave <group name>"

    override fun run(p: Player, args: Array<String>) {
        if (args.isEmpty()) {
            p.sendMessage(Utils.color("&c$syntax"))
            return
        }

        if (!GroupUtils.validateGroupName(args[0])) {
            p.sendMessage(Utils.color("&cInvalid group name"))
            return
        }

        val group = GroupManager.getGroup(args[0])

        if (group == null) {
            p.sendMessage(Utils.color("&cThat group does not exist"))
            return
        }

        if (!group.isMember(p.uniqueId)) {
            p.sendMessage(Utils.color("&cYou can't leave a group you aren't in, silly!"))
            return
        }

        if (group.owner == p.uniqueId) {
            p.sendMessage(Utils.color("&cYou can't leave your own group, to delete this group use /group delete"))
            return
        }

        group.members.remove(p.uniqueId)
        group.activeSpeakers.remove(p.uniqueId)
        p.sendMessage(Utils.color("You have now left this group, see you!"))

        if (group.members.size == 0) {
            GroupManager.deleteGroup(group)
        }
    }
}