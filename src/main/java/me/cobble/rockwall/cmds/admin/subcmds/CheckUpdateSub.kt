package me.cobble.rockwall.cmds.admin.subcmds

import me.cobble.rockwall.rockwall.Rockwall
import me.cobble.rockwall.utils.RockwallBaseCommand
import me.cobble.rockwall.utils.TextUtils
import org.bukkit.entity.Player

class CheckUpdateSub(private val plugin: Rockwall) : RockwallBaseCommand {
    override val name: String
        get() = "update"
    override val descriptor: String
        get() = "Checks for updates"
    override val syntax: String
        get() = "[label] $name [download]"

    override fun run(p: Player, args: Array<String>) {
        if (args.isEmpty()) {
            if (plugin.getUpdateUtils().updateAvailable()) {
                plugin.getUpdateUtils().sendUpdateAvailableMsg(p)
            } else {
                p.sendMessage(TextUtils.color("&7No updates available!"))
            }
        }

        if (args.size == 1) {
            if (args[0].equals("download", ignoreCase = true)) {
                p.sendMessage(TextUtils.color("&cBETA FEATURE: Expect bugs or errors"))
                if (plugin.getUpdateUtils().updateAvailable()) {
                    plugin.getUpdateUtils().downloadUpdate(p)
                } else {
                    p.sendMessage(TextUtils.color("&cNo update to download"))
                }
            }
        }
    }
}
