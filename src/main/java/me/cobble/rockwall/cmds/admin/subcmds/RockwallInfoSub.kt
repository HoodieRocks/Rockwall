package me.cobble.rockwall.cmds.admin.subcmds

import me.cobble.rockwall.rockwall.Rockwall
import me.cobble.rockwall.utils.ChatUtils
import me.cobble.rockwall.utils.ColorUtils
import me.cobble.rockwall.utils.ColorUtils.sendColoredMessage
import me.cobble.rockwall.utils.models.RockwallBaseCommand
import me.cobble.rockwall.utils.parties.PartyUtils
import org.bukkit.entity.Player

class RockwallInfoSub(private val plugin: Rockwall) : RockwallBaseCommand {
    override val name: String
        get() = "info"
    override val descriptor: String
        get() = "Information about Rockwall"
    override val syntax: String
        get() = "[label] $name"

    override fun run(p: Player, args: Array<String>): Boolean {
        val desc = plugin.description
        p.sendColoredMessage("\n&e&lRockwall Info\n\n&7Version: &f${desc.version}\n&7Authors: &f${desc.authors}")
        p.sendColoredMessage("&7PAPI Support: ${colorize(ColorUtils.placeholderAPIPresent)}")
        p.sendColoredMessage("&7Global Chat enabled: ${colorize(ChatUtils.isGlobalChatEnabled())}")
        p.sendColoredMessage("&7Parties enabled: ${colorize(PartyUtils.arePartiesEnabled())}")
        if (plugin.getUpdateUtils().updateAvailable()) {
            plugin.getUpdateUtils().sendUpdateAvailableMsg(p)
        } else {
            p.sendMessage(ColorUtils.color("&7No updates available!"))
        }
        p.sendMessage("\n")
        return true
    }

    private fun colorize(bool: Boolean): String {
        return if (bool) "&dtrue" else "&cfalse"
    }
}
