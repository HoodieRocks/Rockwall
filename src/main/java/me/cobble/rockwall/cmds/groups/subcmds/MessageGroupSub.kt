package me.cobble.rockwall.cmds.groups.subcmds

import me.cobble.rockwall.utils.RockwallBaseCommand
import me.cobble.rockwall.utils.Utils
import me.cobble.rockwall.utils.groups.GroupManager
import me.cobble.rockwall.utils.groups.GroupUtils
import me.cobble.rockwall.utils.groups.models.AdminGroup
import org.bukkit.entity.Player

class MessageGroupSub : RockwallBaseCommand() {
    override val name: String
        get() = "msg"
    override val descriptor: String
        get() = "Message your group"
    override val syntax: String
        get() = "/g msg <group name>"

    override fun run(p: Player, args: Array<String>) {
        if (args.isEmpty() || (args.size == 1 && args[0].equals("global", ignoreCase = true))) {
            p.sendMessage(Utils.color("&aYou are now speaking in the global chat"))
            GroupUtils.changeChatSpeaker(p.uniqueId, null)
        } else {
            val groupName = args[0]
            if (GroupUtils.validateGroupName(groupName)) {
                val group = GroupManager.getGroup(groupName)
                if (GroupManager.groupExists(groupName) && ((group is AdminGroup && p.hasPermission("rockwall.admin.join")) || group!!.isMember(
                        p.uniqueId
                    ))
                ) {
                    if (group.isSpeaking(p.uniqueId)) p.sendMessage(Utils.color("&cYou are already talking in this group")) else {
                        group.addSpeaker(p.uniqueId)
                        GroupUtils.changeChatSpeaker(p.uniqueId, group)
                        p.sendMessage(Utils.color("&aYou are now talking in $groupName."))
                    }
                } else {
                    p.sendMessage(Utils.color("&cThat group does not exist"))
                }
            }
        }
    }
}