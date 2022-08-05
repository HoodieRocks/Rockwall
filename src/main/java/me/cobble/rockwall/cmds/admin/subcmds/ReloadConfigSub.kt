package me.cobble.rockwall.cmds.admin.subcmds

import me.cobble.rockwall.config.Config
import me.cobble.rockwall.config.Messages
import me.cobble.rockwall.utils.RockwallBaseCommand
import me.cobble.rockwall.utils.Utils
import org.bukkit.entity.Player

class ReloadConfigSub : RockwallBaseCommand() {
    override val name: String
        get() = "reload"
    override val descriptor: String
        get() = "Reloads the config"
    override val syntax: String
        get() = "[label] reload"

    override fun run(p: Player, args: Array<String>) {
        Config.reload()
        Messages.reload()
        p.sendMessage(Utils.color("&aConfig reloaded"))
    }
}