package me.cobble.rockwall.cmds.admin.subcmds

import me.cobble.rockwall.rockwall.Rockwall
import me.cobble.rockwall.utils.Formats
import me.cobble.rockwall.utils.RockwallBaseCommand
import org.bukkit.entity.Player

class CheckUpdateSub(private val plugin: Rockwall) : RockwallBaseCommand() {
    override val name: String
        get() = "update"
    override val descriptor: String
        get() = "Checks for updates (Coming soon!)"
    override val syntax: String
        get() = "[label] $name"

    override fun run(p: Player, args: Array<String>) {
        if (plugin.getUpdateUtils().updateAvailable()) {
            plugin.getUpdateUtils().sendUpdateAvailableMsg(p)
        } else {
            p.sendMessage(Formats.color("&7No updates available!"))
        }
    }
}
