package me.cobble.rockwall.cmds.global

import me.cobble.rockwall.utils.Utils
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.entity.Player

class ClearChatCommand : BukkitCommand("clearchat", "Clears chat", "", listOf("cc")) {

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            if (sender.hasPermission("rockwall.clearchat")) {
                for (i in (0..100)) {
                    for (player: Player in Bukkit.getOnlinePlayers()) {
                        player.sendMessage("\n")
                    }
                }
            } else {
                sender.sendMessage(Utils.color("&cYou do not have permission to use this command"))
                return false
            }
        }
        return false
    }
}