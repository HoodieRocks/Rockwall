package me.cobble.rockwall.cmds.chat

import me.cobble.rockwall.config.models.Features
import me.cobble.rockwall.utils.ColorUtils
import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand

class EmojisCommand : BukkitCommand("emojis", "Shows all the available emojis for this server", "/emojis", listOf()) {

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<out String>): Boolean {
        sender.sendMessage(ColorUtils.color("&e&lEmojis"))
        for (emoji in Features.getAllEmojis()) {
            val name = emoji.replaceFirstChar { it.uppercaseChar() }
            sender.sendMessage(ColorUtils.color("&7${name}: ${Features.getEmoji(emoji).orElseThrow()}"))
        }
        return true
    }
}