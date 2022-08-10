package me.cobble.rockwall.cmds.parties.subcmds

import me.cobble.rockwall.config.Messages
import me.cobble.rockwall.utils.RockwallBaseCommand
import me.cobble.rockwall.utils.Utils
import me.cobble.rockwall.utils.parties.PartyUtils
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
        if(PartyUtils.getPartyBySpeaking(p.uniqueId) == null) p.sendMessage(Messages.getPartyMsg("errors.not-in-a-party"))
        else {
            val party = PartyUtils.getPartyBySpeaking(p.uniqueId)!!
            p.sendMessage(Utils.color("\n\n&e&lParty &7Members\n\n"))
            p.sendMessage(Utils.color("&7(&cOwner&7) &f${Bukkit.getPlayer(party.owner)}"))
            for (member in party.members) {
                if(party.owner == member) return
                p.sendMessage(Utils.color("&7${Bukkit.getPlayer(party.owner)}"))
            }
        }
    }
}