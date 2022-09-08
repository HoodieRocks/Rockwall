package me.cobble.rockwall.cmds.parties.subcmds

import me.cobble.rockwall.config.models.Messages
import me.cobble.rockwall.config.models.PartyType
import me.cobble.rockwall.utils.TextUtils
import me.cobble.rockwall.utils.TextUtils.containsSpecialCharacters
import me.cobble.rockwall.utils.models.RockwallBaseCommand
import me.cobble.rockwall.utils.parties.PartyManager
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class CreatePartySub : RockwallBaseCommand {
    override val name: String
        get() = "create"
    override val descriptor: String
        get() = "Creates a new party"
    override val syntax: String
        get() = "[label] create <name> [type]"

    override fun run(p: Player, args: Array<String>): Boolean {
        if (args.isEmpty()) {
            p.sendMessage(TextUtils.color("&c${syntax.replace("[label]", "/party")}"))
            return false
        } else {
            if (args[0].containsSpecialCharacters()) {
                p.sendMessage(Messages.getPartyMsg("errors.no-special-characters"))
                return false
            }

            if (PartyManager.doesPartyExists(p.uniqueId)) {
                p.sendMessage(Messages.getPartyMsg("errors.party-limit-reached"))
                return false
            }

            when (args.size) {
                2 -> {
                    val type = PartyType.valueOf(args[1].uppercase())
                    if (type == PartyType.ADMIN && !p.hasPermission("rockwall.admin.create")) {
                        p.sendMessage(Messages.getPermissionString("no-admin-create-perm"))
                        return false
                    }
                    PartyManager.createParty(p.uniqueId, args[0], PartyType.valueOf(args[1].uppercase().trim()))
                    addPlayers()
                }

                1 -> {
                    PartyManager.createParty(p.uniqueId, args[0], PartyType.NORMAL)
                }

                else -> {
                    p.sendMessage(Messages.getGeneralError("too-many-args"))
                    return false
                }
            }
            PartyManager.getParty(p.uniqueId)!!.addMember(p.uniqueId)

            p.sendMessage(Messages.getPartyMsg("creation", PartyManager.getParty(p.uniqueId)!!))
            return true
        }
    }

    private fun addPlayers() {
        for (party in PartyManager.getAllAdminParties().get()) {
            for (player in Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("rockwall.admin.join")) party.addMember(player.uniqueId)
            }
        }
    }
}
