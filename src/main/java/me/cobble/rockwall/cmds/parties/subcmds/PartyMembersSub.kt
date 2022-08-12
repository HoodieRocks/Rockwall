package me.cobble.rockwall.cmds.parties.subcmds

import me.cobble.rockwall.config.Messages
import me.cobble.rockwall.utils.Formats
import me.cobble.rockwall.utils.RockwallBaseCommand
import me.cobble.rockwall.utils.parties.Parties
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class PartyMembersSub : RockwallBaseCommand() {
    override val name: String
        get() = "members"
    override val descriptor: String
        get() = "Shows your party's members"
    override val syntax: String
        get() = "[label] members"

    override fun run(p: Player, args: Array<String>) {
        if (Parties.getPartyBySpeaking(p.uniqueId) == null) p.sendMessage(Messages.getPartyMsg("errors.not-speaking-in-party"))
        else {
            val party = Parties.getPartyBySpeaking(p.uniqueId)!!
            p.sendMessage(Formats.color("\n\n&e&lParty &7Members\n\n"))
            p.sendMessage(Formats.color("&7(&cOwner&7) &f${Bukkit.getPlayer(party.owner)!!.name}"))
            for (member in party.members) {
                if (party.owner == member) return
                p.sendMessage(Formats.color("&7${Bukkit.getPlayer(party.owner)!!.name}"))
            }
        }
    }
}
