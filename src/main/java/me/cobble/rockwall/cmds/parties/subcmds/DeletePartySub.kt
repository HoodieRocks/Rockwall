package me.cobble.rockwall.cmds.parties.subcmds

import me.cobble.rockwall.config.Messages
import me.cobble.rockwall.utils.RockwallBaseCommand
import me.cobble.rockwall.utils.parties.PartyManager
import org.bukkit.entity.Player

class DeletePartySub : RockwallBaseCommand() {
    override val name: String
        get() = "delete"
    override val descriptor: String
        get() = "Deletes a group. \n(You must be the group owner to do this)"
    override val syntax: String
        get() = "/party delete"

    override fun run(p: Player, args: Array<String>) {
        val group = PartyManager.getParty(p.uniqueId)

        if (group == null) {
            p.sendMessage(Messages.getPartyMsg("errors.no-group-for-deletion"))
            return
        }

        if (group.owner == p.uniqueId) {
            PartyManager.deleteParty(group)
        }
    }
}