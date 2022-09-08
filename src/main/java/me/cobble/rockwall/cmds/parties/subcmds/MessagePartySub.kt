package me.cobble.rockwall.cmds.parties.subcmds

import me.cobble.rockwall.config.models.Messages
import me.cobble.rockwall.utils.models.RockwallBaseCommand
import me.cobble.rockwall.utils.parties.PartyManager
import me.cobble.rockwall.utils.parties.PartyUtils
import me.cobble.rockwall.utils.parties.models.AdminParty
import org.bukkit.entity.Player

class MessagePartySub : RockwallBaseCommand {
    override val name: String
        get() = "msg"
    override val descriptor: String
        get() = "Message your party"
    override val syntax: String
        get() = "[label] msg <party name>"

    override fun run(p: Player, args: Array<String>): Boolean {
        if (args.isEmpty() || (args.size == 1 && args[0].equals("global", ignoreCase = true))) {
            p.sendMessage(Messages.getPartyMsg("messaging-global"))
            PartyUtils.removeOldSpeakingParty(p.uniqueId, null)
            return true
        } else {
            val partyName = args[0]
            if (PartyUtils.isPartyNameValid(partyName)) {
                var returnBoolean = false
                PartyManager.getParty(partyName).thenAccept {
                    if (!p.hasPermission("rockwall.admin.joinany") || !it!!.isMember(p.uniqueId)) {
                        p.sendMessage(Messages.getPermissionString("no-perm-party"))
                        return@thenAccept
                    }

                    if (
                        PartyManager.doesPartyExists(partyName) &&
                        ((it is AdminParty && p.hasPermission("rockwall.admin.join")) ||
                                (p.hasPermission("rockwall.admin.joinany") || it.isMember(p.uniqueId)
                                        ))
                    ) {
                        if (it.isSpeaking(p.uniqueId)) p.sendMessage(Messages.getPartyMsg("already-speaking")) else {
                            it.addSpeaker(p.uniqueId)
                            PartyUtils.removeOldSpeakingParty(p.uniqueId, it)
                            p.sendMessage(Messages.getPartyMsg("now-speaking", it))
                            returnBoolean = true
                        }
                    } else {
                        p.sendMessage(Messages.getPartyMsg("errors.404"))
                    }
                }
                return returnBoolean

            } else {
                p.sendMessage(Messages.getPartyMsg("errors.invalid"))
                return false
            }
        }
    }
}
