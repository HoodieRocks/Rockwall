package me.cobble.rockwall.cmds.subcmds.groups

import me.cobble.rockwall.utils.RockwallBaseCommand
import me.cobble.rockwall.utils.Utils
import me.cobble.rockwall.utils.groups.GroupManager
import me.cobble.rockwall.utils.groups.GroupUtils
import org.bukkit.entity.Player

class DenyInviteSub : RockwallBaseCommand() {
    override val name: String
        get() = "deny"
    override val descriptor: String
        get() = "Denies pending invites"
    override val syntax: String
        get() = "/g deny <group name>"

    override fun run(p: Player, args: Array<String>) {
        if (args.isEmpty()) {
            p.sendMessage(Utils.color("&c$syntax"))
        } else {
            if (GroupUtils.validateGroupName(args[0]) && GroupManager.groupExists(args[0])) {
                val group = GroupManager.getGroup(args[0])
                if (group!!.isInvited(p.uniqueId)) {
                    GroupUtils.inviteToMember(p.uniqueId, group)
                    p.sendMessage("&aYou are now a member of ${group.alias}")
                }
            }
        }
    }
}