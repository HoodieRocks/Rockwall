package me.cobble.rockwall.cmds.admin.subcmds

import me.cobble.rockwall.rockwall.Rockwall
import me.cobble.rockwall.utils.ChatUtils
import me.cobble.rockwall.utils.TextUtils
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
        val description = plugin.description
        p.sendMessage(TextUtils.color("\n&e&lRockwall &7Information\n\n&7Version: &f${description.version}\n&7Authors: &f${description.authors}"))
        p.sendMessage(TextUtils.color("&7PAPI Support: ${if (TextUtils.placeholderAPIPresent) "&d" else "&c"}${TextUtils.placeholderAPIPresent}"))
        p.sendMessage(TextUtils.color("&7Global Chat enabled: ${if (ChatUtils.isGlobalChatEnabled()) "&d" else "&c"}${ChatUtils.isGlobalChatEnabled()}"))
        p.sendMessage(TextUtils.color("&7Parties enabled: ${if (PartyUtils.arePartiesEnabled()) "&d" else "&c"}${PartyUtils.arePartiesEnabled()}"))
        if (plugin.getUpdateUtils().updateAvailable()) {
            plugin.getUpdateUtils().sendUpdateAvailableMsg(p)
        } else {
            p.sendMessage(TextUtils.color("&7No updates available!"))
        }
        p.sendMessage("\n")
        return true
    }
}
