package me.cobble.rockwall.cmds.admin.subcmds

import me.cobble.rockwall.rockwall.Rockwall
import me.cobble.rockwall.utils.Formats
import me.cobble.rockwall.utils.RockwallBaseCommand
import org.bukkit.entity.Player

class RockwallInfoSub(private val plugin: Rockwall) : RockwallBaseCommand() {
    override val name: String
        get() = "info"
    override val descriptor: String
        get() = "Information about Rockwall"
    override val syntax: String
        get() = "[label] $name"

    override fun run(p: Player, args: Array<String>) {
        val description = plugin.description
        p.sendMessage(Formats.color("\n&e&lRockwall &7Information\n\n&7Version: &f${description.version}\n&7Authors: &f${description.authors}"))
        p.sendMessage(Formats.color("&7PAPI Support: ${if (Formats.placeholderAPIPresent) "&d" else "&c"}${Formats.placeholderAPIPresent}"))
        p.sendMessage("\n")
    }
}
