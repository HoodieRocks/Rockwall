package me.cobble.rockwall.cmds.groups.subcmds

import me.cobble.rockwall.config.Messages
import me.cobble.rockwall.utils.RockwallBaseCommand
import me.cobble.rockwall.utils.parties.PartyManager
import me.cobble.rockwall.utils.parties.PartyUtils
import me.cobble.rockwall.utils.parties.models.AdminParty
import org.bukkit.entity.Player

class MessagePartySub : RockwallBaseCommand() {
    override val name: String
        get() = "msg"
    override val descriptor: String
        get() = "Message your group"
    override val syntax: String
        get() = "/party msg <group name>"

    override fun run(p: Player, args: Array<String>) {
        if (args.isEmpty() || (args.size == 1 && args[0].equals("global", ignoreCase = true))) {
            p.sendMessage(Messages.getGroupString("messaging-global"))
            PartyUtils.changeChatSpeaker(p.uniqueId, null)
        } else {
            val groupName = args[0]
            if (PartyUtils.validateGroupName(groupName)) {
                val group = PartyManager.getGroup(groupName)
                if (PartyManager.groupExists(groupName) && ((group is AdminParty && p.hasPermission("rockwall.admin.join")) || group!!.isMember(
                        p.uniqueId
                    ))
                ) {
                    if (group.isSpeaking(p.uniqueId)) p.sendMessage(Messages.getGroupString("already-speaking")) else {
                        group.addSpeaker(p.uniqueId)
                        PartyUtils.changeChatSpeaker(p.uniqueId, group)
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