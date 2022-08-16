package me.cobble.rockwall.cmds.admin.subcmds

import me.cobble.rockwall.rockwall.Rockwall
import me.cobble.rockwall.utils.ChatUtils
import me.cobble.rockwall.utils.Formats
import me.cobble.rockwall.utils.RockwallBaseCommand
import me.cobble.rockwall.utils.parties.Parties
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
        p.sendMessage(Formats.color("&7Global Chat enabled: ${if (Formats.placeholderAPIPresent) "&d" else "&c"}${ChatUtils.isGlobalChatEnabled()}"))
        p.sendMessage(Formats.color("&7Parties enabled: ${if (Formats.placeholderAPIPresent) "&d" else "&c"}${Parties.arePartiesEnabled()}"))
        if (plugin.getUpdateUtils().updateAvailable()) {
            plugin.getUpdateUtils().sendUpdateAvailableMsg(p)
        } else {
            p.sendMessage(Formats.color("&7No updates available!"))
        }
        p.sendMessage("\n")
    }
}
