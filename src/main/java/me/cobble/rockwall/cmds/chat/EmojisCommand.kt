package me.cobble.rockwall.cmds.chat

import me.cobble.rockwall.config.Emojis
import me.cobble.rockwall.utils.Formats
import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.entity.Player

class EmojisCommand : BukkitCommand("emojis", "Shows all the available emojis for this server", "/emojis", listOf()) {

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            sender.sendMessage(Formats.color("&e&lEmojis"))
            for (emoji in Emojis.getAllEmojis()) {
                sender.sendMessage(Formats.color("&7${(emoji as String).replaceFirstChar { it.uppercaseChar() }}: ${Emojis.getEmoji(emoji as String)}"))
            }
            return true
        }
        return false
    }
}