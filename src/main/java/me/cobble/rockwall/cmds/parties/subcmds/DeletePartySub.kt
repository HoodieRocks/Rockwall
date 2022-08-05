package me.cobble.rockwall.cmds.parties.subcmds

import me.cobble.rockwall.config.Messages
import me.cobble.rockwall.utils.RockwallBaseCommand
import me.cobble.rockwall.utils.parties.PartyManager
import org.bukkit.entity.Player

class DeletePartySub(private val label: String) : RockwallBaseCommand() {
    override val name: String
        get() = "delete"
    override val descriptor: String
        get() = "Deletes a party. (You must be the party owner to do this)"
    override val syntax: String
        get() = "[label] delete"

    override fun run(p: Player, args: Array<String>) {
        val party = PartyManager.getParty(p.uniqueId)

        if (party == null) {
            p.sendMessage(Messages.getPartyMsg("errors.no-party-for-deletion"))
            return
        }

        if (party.owner == p.uniqueId) {
            PartyManager.deleteParty(party)
        }
    }
}