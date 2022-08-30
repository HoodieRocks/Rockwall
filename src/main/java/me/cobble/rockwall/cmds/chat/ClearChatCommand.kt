package me.cobble.rockwall.cmds.chat

import me.cobble.rockwall.utils.TextUtils
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.entity.Player

class ClearChatCommand : BukkitCommand("clearchat", "Clears chat", "", listOf("cc")) {

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<out String>): Boolean {
        if (sender.hasPermission("rockwall.clearchat")) {
            for (player: Player in Bukkit.getOnlinePlayers()) {
                for (i in (0..100)) {
                    player.sendMessage("\n")
                }
                return true
            }
        } else {
            sender.sendMessage(TextUtils.color("&cYou do not have permission to use this command"))
            return false
        }
        return false
    }
}
