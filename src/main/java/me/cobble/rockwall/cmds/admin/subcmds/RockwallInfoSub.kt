package me.cobble.rockwall.cmds.admin.subcmds

import me.cobble.rockwall.rockwall.Rockwall
import me.cobble.rockwall.utils.RockwallBaseCommand
import me.cobble.rockwall.utils.Utils
import org.bukkit.entity.Player

class RockwallInfoSub(private val plugin: Rockwall) : RockwallBaseCommand() {
    override val name: String
        get() = "info"
    override val descriptor: String
        get() = "Information about Rockwall"
    override val syntax: String
        get() = "/rockwall $name"

    override fun run(p: Player, args: Array<String>) {
        val description = plugin.description
        p.sendMessage(Utils.color("\n&e&lRockwall &7Information\n\n&7Version: &f${description.version}\n&7Authors: &f${description.authors}"))
        p.sendMessage(Utils.color("&7PAPI Support: ${if (Utils.placeholderAPIPresent) "&d" else "&c"}${Utils.placeholderAPIPresent}"))
        p.sendMessage("\n")
    }
}