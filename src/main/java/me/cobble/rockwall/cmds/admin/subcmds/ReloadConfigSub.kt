package me.cobble.rockwall.cmds.admin.subcmds

import me.cobble.rockwall.rockwall.Config
import me.cobble.rockwall.utils.RockwallBaseCommand
import me.cobble.rockwall.utils.Utils
import org.bukkit.entity.Player

class ReloadConfigSub : RockwallBaseCommand() {
    override val name: String
        get() = "reload"
    override val descriptor: String
        get() = "Reloads the config"
    override val syntax: String
        get() = "/rockwall reload"

    override fun run(p: Player, args: Array<String>) {
        Config.reload()
        p.sendMessage(Utils.color("&aConfig reloaded"))
    }
}