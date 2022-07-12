package me.cobble.rockwall.cmds.admin.subcmds

import me.cobble.rockwall.utils.RockwallBaseCommand
import me.cobble.rockwall.utils.Utils
import org.bukkit.entity.Player

class CheckUpdateSub : RockwallBaseCommand() {
    override val name: String
        get() = "update"
    override val descriptor: String
        get() = "Checks for updates (Coming soon!)"
    override val syntax: String
        get() = "/rockwall $name"

    override fun run(p: Player, args: Array<String>) {
        // TODO: Finish when uploaded to spigot
        p.sendMessage(Utils.color("&7Coming soon!"))
    }
}