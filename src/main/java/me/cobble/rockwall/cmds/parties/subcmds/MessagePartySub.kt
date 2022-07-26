package me.cobble.rockwall.cmds.parties.subcmds

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
            p.sendMessage(Messages.getPartyMsg("messaging-global"))
            PartyUtils.removeOldSpeakingParty(p.uniqueId, null)
        } else {
            val groupName = args[0]
            if (PartyUtils.validatePartyName(groupName)) {
                val group = PartyManager.getParty(groupName)
                if (PartyManager.partyExists(groupName) && ((group is AdminParty && p.hasPermission("rockwall.admin.join")) || group!!.isMember(
                        p.uniqueId
                    ))
                ) {
                    if (group.isSpeaking(p.uniqueId)) p.sendMessage(Messages.getPartyMsg("already-speaking")) else {
                        group.addSpeaker(p.uniqueId)
                        PartyUtils.removeOldSpeakingParty(p.uniqueId, group)
                        p.sendMessage(Messages.getPartyMsg("now-speaking", group))
                    }
                } else {
                    p.sendMessage(Messages.getPartyMsg("errors.404"))
                }
            } else {
                p.sendMessage(Messages.getPartyMsg("errors.invalid"))
            }
        }
    }
}