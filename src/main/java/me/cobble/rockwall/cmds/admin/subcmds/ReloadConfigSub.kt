package me.cobble.rockwall.cmds.admin.subcmds

import me.cobble.rockwall.config.Config
import me.cobble.rockwall.utils.Formats
import me.cobble.rockwall.utils.RockwallBaseCommand
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
        p.sendMessage(Formats.color("&aConfig reloaded"))
    }
}
