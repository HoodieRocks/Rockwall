package me.cobble.rockwall.cmds.admin.subcmds

import me.cobble.rockwall.config.Config
import me.cobble.rockwall.utils.TextUtils
import me.cobble.rockwall.utils.models.RockwallBaseCommand
import org.bukkit.entity.Player

class ReloadConfigSub : RockwallBaseCommand {
    override val name: String
        get() = "reload"
    override val descriptor: String
        get() = "Reloads the config"
    override val syntax: String
        get() = "[label] reload"

    override fun run(p: Player, args: Array<String>): Boolean {
        Config.reload()
        p.sendMessage(TextUtils.color("&aConfig reloaded"))
        return true
    }
}
