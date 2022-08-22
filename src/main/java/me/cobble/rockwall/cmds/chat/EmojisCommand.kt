package me.cobble.rockwall.cmds.chat

import me.cobble.rockwall.utils.Formats
import me.cobble.rockwall.utils.parties.parties.Features
import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.entity.Player

class EmojisCommand : BukkitCommand("emojis", "Shows all the available emojis for this server", "/emojis", listOf()) {

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            sender.sendMessage(Formats.color("&e&lEmojis"))
            for (emoji in Features.getAllEmojis()) {
                sender.sendMessage(Formats.color("&7${
                    (emoji as String)
                        .replaceFirstChar { it.uppercaseChar() }
                }: ${Features.getEmoji(emoji)}"
                )
                )
            }
            return true
        }
        return false
    }
}