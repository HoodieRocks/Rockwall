package me.cobble.rockwall.cmds.groups.subcmds

import me.cobble.rockwall.rockwall.Messages
import me.cobble.rockwall.utils.RockwallBaseCommand
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
            p.sendMessage(Messages.getGroupString("messaging-global"))
            GroupUtils.changeChatSpeaker(p.uniqueId, null)
        } else {
            val groupName = args[0]
            if (GroupUtils.validateGroupName(groupName)) {
                val group = GroupManager.getGroup(groupName)
                if (GroupManager.groupExists(groupName) && ((group is AdminGroup && p.hasPermission("rockwall.admin.join")) || group!!.isMember(
                        p.uniqueId
                    ))
                ) {
                    if (group.isSpeaking(p.uniqueId)) p.sendMessage(Messages.getGroupString("already-speaking")) else {
                        group.addSpeaker(p.uniqueId)
                        GroupUtils.changeChatSpeaker(p.uniqueId, group)
                        p.sendMessage(Messages.getGroupString("now-speaking", group))
                    }
                } else {
                    p.sendMessage(Messages.getGroupString("errors.404"))
                }
            } else {
                p.sendMessage(Messages.getGroupString("errors.invalid"))
            }
        }
    }
}