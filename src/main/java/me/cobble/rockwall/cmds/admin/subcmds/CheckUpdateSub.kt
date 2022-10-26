package me.cobble.rockwall.cmds.admin.subcmds

import me.cobble.rockwall.config.models.Messages
import me.cobble.rockwall.rockwall.Rockwall
import me.cobble.rockwall.utils.ColorUtils
import me.cobble.rockwall.utils.models.RockwallBaseCommand
import org.bukkit.entity.Player

class CheckUpdateSub(private val plugin: Rockwall) : RockwallBaseCommand {
    override val name: String
        get() = "update"
    override val descriptor: String
        get() = "Checks for updates"
    override val syntax: String
        get() = "[label] $name [download]"

    override fun run(p: Player, args: Array<String>): Boolean {
        if (args.isEmpty()) {
            if (plugin.getUpdateUtils().updateAvailable()) {
                plugin.getUpdateUtils().sendUpdateAvailableMsg(p)
            } else {
                p.sendMessage(ColorUtils.color("&7No updates available!"))
            }
        }

        if (args.size == 1) {
            return if (args[0].equals("download", ignoreCase = true)) {
                p.sendMessage(ColorUtils.color("&cBETA FEATURE: Expect bugs or errors"))
                if (plugin.getUpdateUtils().updateAvailable()) {
                    plugin.getUpdateUtils().downloadUpdate(p)
                    true
                } else {
                    p.sendMessage(ColorUtils.color("&cNo update to download"))
                    true
                }
            } else {
                p.sendMessage(Messages.getGeneralError("unknown-argument"))
                false
            }
        } else {
            p.sendMessage(Messages.getGeneralError("too-many-args"))
            return true
        }
    }
}
